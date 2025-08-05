# Simple test to check basic functionality
Write-Host "=== Simple API Test ===" -ForegroundColor Green

# Test 1: Check if application is responding
Write-Host "`n1. Testing application health..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" -Method GET
    Write-Host "Application is responding" -ForegroundColor Green
} catch {
    Write-Host "Application error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Try to register a simple user
Write-Host "`n2. Testing user registration..." -ForegroundColor Yellow
$registerBody = @{
    userId = "simpleuser"
    userNm = "Simple User"
    userPwd = "123"
    eml = "simple@gmail.com"
    tel = "1234567890"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -ContentType "application/json" -Body $registerBody
    Write-Host "Registration successful!" -ForegroundColor Green
    Write-Host "Response: $($response | ConvertTo-Json)" -ForegroundColor Cyan
} catch {
    Write-Host "Registration failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response body: $responseBody" -ForegroundColor Red
    }
}

Write-Host "`n=== Test Complete ===" -ForegroundColor Green 