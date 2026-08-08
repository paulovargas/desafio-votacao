param(
    [string]$BaseUrl = "http://localhost:8080",
    [long]$PautaId = 1,
    [int]$Requests = 1000
)

$ErrorActionPreference = "Stop"

if ($Requests -le 0) {
    throw "Requests deve ser maior que zero."
}

$endpoint = "$BaseUrl/api/v1/pautas/$PautaId/resultado"
$success = 0
$failures = 0
$statusCodes = @{}
$watch = [System.Diagnostics.Stopwatch]::StartNew()

for ($i = 1; $i -le $Requests; $i++) {
    try {
        $response = Invoke-WebRequest -Uri $endpoint -Method Get -UseBasicParsing
        $statusCode = [string]$response.StatusCode

        if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 300) {
            $success++
        } else {
            $failures++
        }
    } catch {
        $failures++

        if ($_.Exception.Response -and $_.Exception.Response.StatusCode) {
            $statusCode = [string][int]$_.Exception.Response.StatusCode
        } else {
            $statusCode = "ERROR"
        }
    }

    if (-not $statusCodes.ContainsKey($statusCode)) {
        $statusCodes[$statusCode] = 0
    }

    $statusCodes[$statusCode]++
}

$watch.Stop()
$elapsedMs = [Math]::Max($watch.Elapsed.TotalMilliseconds, 1)

[PSCustomObject]@{
    endpoint = $endpoint
    requests = $Requests
    success = $success
    failures = $failures
    elapsedMs = [Math]::Round($watch.Elapsed.TotalMilliseconds, 2)
    averageMs = [Math]::Round($watch.Elapsed.TotalMilliseconds / $Requests, 2)
    requestsPerSecond = [Math]::Round(($Requests / $elapsedMs) * 1000, 2)
    statusCodes = $statusCodes
} | ConvertTo-Json -Depth 4
