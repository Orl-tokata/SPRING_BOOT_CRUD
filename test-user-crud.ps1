# Test UserController CRUD Operations
$baseUrl = "http://localhost:8080/api/users"

Write-Host "=== Testing UserController CRUD Operations ===" -ForegroundColor Green

# First, let's get a JWT token by logging in
Write-Host "`n1. Getting JWT token..." -ForegroundColor Yellow
$loginBody = @{
    userId = "testuser2"
    userPwd = "123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    $token = $loginResponse.data.token
    Write-Host "Token obtained successfully" -ForegroundColor Green
} catch {
    Write-Host "Failed to get token: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

# 2. Test GET all users
Write-Host "`n2. Testing GET all users..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/findall" -Method GET -Headers $headers
    Write-Host "Success! Found $($response.data.Count) users" -ForegroundColor Green
    $response.data | ForEach-Object { Write-Host "  - $($_.bizKey): $($_.userId) ($($_.userNm))" }
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. Test GET user by userId
Write-Host "`n3. Testing GET user by userId..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/findbyuserid/testuser2" -Method GET -Headers $headers
    Write-Host "Success! User found: $($response.data.userNm)" -ForegroundColor Green
    $bizKey = $response.data.bizKey
    Write-Host "  bizKey: $bizKey" -ForegroundColor Cyan
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. Test GET user by bizKey
Write-Host "`n4. Testing GET user by bizKey..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/findbyid/$bizKey" -Method GET -Headers $headers
    Write-Host "Success! User found by bizKey: $($response.data.userNm)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. Test UPDATE user
Write-Host "`n5. Testing UPDATE user..." -ForegroundColor Yellow
$updateBody = @{
    userNm = "Updated Test User 2"
    tel = "0987654329"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/update/$bizKey" -Method PUT -Headers $headers -Body $updateBody
    Write-Host "Success! User updated: $($response.data.userNm)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. Test CREATE new user
Write-Host "`n6. Testing CREATE new user..." -ForegroundColor Yellow
$createBody = @{
    userId = "testuser3"
    userNm = "Test User 3"
    userPwd = "123"
    eml = "testuser3@gmail.com"
    tel = "0987654330"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/create" -Method POST -Headers $headers -Body $createBody
    Write-Host "Success! New user created: $($response.data.userNm) with bizKey: $($response.data.bizKey)" -ForegroundColor Green
    $newBizKey = $response.data.bizKey
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# 7. Test DELETE user (only if we successfully created one)
if ($newBizKey) {
    Write-Host "`n7. Testing DELETE user..." -ForegroundColor Yellow
    try {
        $response = Invoke-RestMethod -Uri "$baseUrl/delete/$newBizKey" -Method DELETE -Headers $headers
        Write-Host "Success! User deleted" -ForegroundColor Green
    } catch {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=== CRUD Testing Complete ===" -ForegroundColor Green 