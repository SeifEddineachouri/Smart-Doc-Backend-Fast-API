param(
    [string]$FastApiBaseUrl = "http://127.0.0.1:8000",
    [string]$SpringBaseUrl = "http://127.0.0.1:8087",
    [string]$ServiceToken = "",
    [int]$TimeoutSec = 10,
    [switch]$SkipFastApi,
    [switch]$SkipSpring
)

$ErrorActionPreference = "Stop"

function Invoke-HttpCheck {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers,
        [object]$Body = $null
    )

    $start = Get-Date
    try {
        $args = @{
            Uri         = $Url
            Method      = $Method
            Headers     = $Headers
            TimeoutSec  = $TimeoutSec
            ErrorAction = "Stop"
        }

        if ($null -ne $Body) {
            $args.ContentType = "application/json"
            $args.Body = ($Body | ConvertTo-Json -Depth 8 -Compress)
        }

        $response = Invoke-RestMethod @args
        $elapsed = [Math]::Round(((Get-Date) - $start).TotalMilliseconds)

        return [pscustomobject]@{
            Check   = $Name
            Status  = "OK"
            Url     = $Url
            Elapsed = "${elapsed}ms"
            Detail  = ($response | ConvertTo-Json -Depth 6 -Compress)
        }
    }
    catch {
        $elapsed = [Math]::Round(((Get-Date) - $start).TotalMilliseconds)
        $err = $_.Exception.Message
        if ($_.ErrorDetails -and $_.ErrorDetails.Message) {
            $err = $_.ErrorDetails.Message
        }

        return [pscustomobject]@{
            Check   = $Name
            Status  = "FAIL"
            Url     = $Url
            Elapsed = "${elapsed}ms"
            Detail  = $err
        }
    }
}

$headers = @{}
if ([string]::IsNullOrWhiteSpace($ServiceToken)) {
    $ServiceToken = @(
        $env:SERVICE_TOKEN,
        $env:APP_AI_SERVICE_TOKEN,
        $env:APP_AI_GATEWAY_SERVICE_TOKEN
    ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -First 1
}

if ([string]::IsNullOrWhiteSpace($ServiceToken)) {
    $tokenFileCandidates = @(
        (Join-Path (Split-Path -Parent $PSScriptRoot) "logs\service-token.txt"),
        (Join-Path (Split-Path -Parent $PSScriptRoot) "..\logs\service-token.txt")
    ) | Select-Object -Unique

    foreach ($candidate in $tokenFileCandidates) {
        if (Test-Path -LiteralPath $candidate) {
            $ServiceToken = (Get-Content -LiteralPath $candidate -Raw).Trim()
            if (-not [string]::IsNullOrWhiteSpace($ServiceToken)) {
                break
            }
        }
    }
}

if (-not [string]::IsNullOrWhiteSpace($ServiceToken)) {
    $headers["Authorization"] = "Bearer $ServiceToken"
}

$results = @()

if (-not $SkipFastApi) {
    $results += Invoke-HttpCheck -Name "fastapi-health" -Method "GET" -Url "$FastApiBaseUrl/health" -Headers $headers

    $results += Invoke-HttpCheck -Name "fastapi-ingest" -Method "POST" -Url "$FastApiBaseUrl/ingest" -Headers $headers -Body @{
        userId     = "u1"
        documentId = "d1"
        content    = "SmartDoc stores and retrieves context for answers."
    }

    $results += Invoke-HttpCheck -Name "fastapi-query" -Method "POST" -Url "$FastApiBaseUrl/query" -Headers $headers -Body @{
        userId      = "u1"
        question    = "What does SmartDoc do?"
        documentIds = @("d1")
    }
}

if (-not $SkipSpring) {
    $results += Invoke-HttpCheck -Name "spring-ai-health" -Method "GET" -Url "$SpringBaseUrl/api/v1/ai/health" -Headers @{}

    $results += Invoke-HttpCheck -Name "spring-ai-ingest" -Method "POST" -Url "$SpringBaseUrl/api/v1/ai/ingest" -Headers @{} -Body @{
        userId     = "u1"
        documentId = "d1"
        content    = "SmartDoc stores and retrieves context for answers."
    }

    $results += Invoke-HttpCheck -Name "spring-ai-questions" -Method "POST" -Url "$SpringBaseUrl/api/v1/ai/questions" -Headers @{} -Body @{
        userId      = "u1"
        question    = "What does SmartDoc do?"
        documentIds = @("d1")
    }
}

$results | Format-Table -AutoSize | Out-String | Write-Host

$failed = @($results | Where-Object { $_.Status -eq "FAIL" }).Count
if ($failed -gt 0) {
    Write-Host "Integration checks completed with $failed failure(s)."
    exit 1
}

Write-Host "Integration checks completed successfully."
exit 0

