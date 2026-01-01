# Technical Requirements Document

## 🧭 System Context
Werkstatt Infinite is a local-first infinite canvas bullet journal with Bauhaus-inspired snap-to-grid functionality. The application uses React with Context API for state management, a Node.js REST API backend, and PostgreSQL for encrypted cloud backup storage.

## 🔌 API Contracts
### Register User
- **Method:** POST
- **Path:** /api/auth/register
- **Auth:** none
- **Request:** {"email":"string","password":"string","username":"string"}
- **Response:** {"user":{"id":"uuid","email":"string","username":"string"},"token":"jwt"}
- **Errors:**
- 400: Invalid email format
- 409: Email already exists

### Login User
- **Method:** POST
- **Path:** /api/auth/login
- **Auth:** none
- **Request:** {"email":"string","password":"string"}
- **Response:** {"user":{"id":"uuid","email":"string","username":"string"},"token":"jwt"}
- **Errors:**
- 401: Invalid credentials

### Create Canvas Entry
- **Method:** POST
- **Path:** /api/canvas
- **Auth:** Bearer JWT
- **Request:** {"title":"string","encrypted_data":"base64","version":"number","created_at":"iso8601"}
- **Response:** {"id":"uuid","user_id":"uuid","title":"string","created_at":"iso8601","updated_at":"iso8601"}
- **Errors:**
- 401: Unauthorized
- 400: Invalid encrypted data format

### Get Canvas Entry
- **Method:** GET
- **Path:** /api/canvas/:id
- **Auth:** Bearer JWT
- **Request:** 
- **Response:** {"id":"uuid","user_id":"uuid","title":"string","encrypted_data":"base64","version":"number","created_at":"iso8601","updated_at":"iso8601"}
- **Errors:**
- 401: Unauthorized
- 404: Canvas not found

### Update Canvas Entry
- **Method:** PUT
- **Path:** /api/canvas/:id
- **Auth:** Bearer JWT
- **Request:** {"title":"string","encrypted_data":"base64","version":"number"}
- **Response:** {"id":"uuid","user_id":"uuid","title":"string","encrypted_data":"base64","version":"number","updated_at":"iso8601"}
- **Errors:**
- 401: Unauthorized
- 404: Canvas not found
- 409: Version conflict

### Delete Canvas Entry
- **Method:** DELETE
- **Path:** /api/canvas/:id
- **Auth:** Bearer JWT
- **Request:** 
- **Response:** {"id":"uuid","deleted":true}
- **Errors:**
- 401: Unauthorized
- 404: Canvas not found

### List Canvas Entries
- **Method:** GET
- **Path:** /api/canvas
- **Auth:** Bearer JWT
- **Request:** 
- **Response:** {"canvases":[{"id":"uuid","title":"string","created_at":"iso8601","updated_at":"iso8601"}],"total":"number"}
- **Errors:**
- 401: Unauthorized

### Sync Canvas Entries
- **Method:** POST
- **Path:** /api/sync
- **Auth:** Bearer JWT
- **Request:** {"last_sync":"iso8601","local_changes":[{"id":"uuid","action":"string","data":{}}]}
- **Response:** {"server_changes":[{"id":"uuid","action":"string","data":{}}],"sync_time":"iso8601"}
- **Errors:**
- 401: Unauthorized
- 400: Invalid sync format

## 🧱 Modules
### AuthModule
- **Responsibilities:**
- User registration
- JWT token generation/validation
- Password hashing
- **Interfaces:**
- register(email, password, username)
- login(email, password)
- verifyToken(token)
- **Depends on:**
- DatabaseModule
- EncryptionModule

### CanvasStorageService
- **Responsibilities:**
- CRUD operations for canvas entries
- Encrypted data storage
- Version tracking
- **Interfaces:**
- create(userId, encryptedData, title)
- findById(id, userId)
- update(id, userId, encryptedData, version)
- delete(id, userId)
- listByUser(userId, pagination)
- **Depends on:**
- DatabaseModule

### EncryptionService
- **Responsibilities:**
- Client-side encryption/decryption
- Key derivation from password
- Data integrity verification
- **Interfaces:**
- encrypt(data, key)
- decrypt(encryptedData, key)
- deriveKey(password, salt)
- **Depends on:**
_None_

### BackupSyncService
- **Responsibilities:**
- Conflict resolution for concurrent edits
- Change tracking
- Incremental sync logic
- **Interfaces:**
- sync(userId, lastSync, localChanges)
- resolveConflict(local, server)
- **Depends on:**
- CanvasStorageService
- DatabaseModule

### CanvasRenderer
- **Responsibilities:**
- Canvas API rendering
- Stroke rendering with 60fps
- Pan and zoom handling
- **Interfaces:**
- render(strokes, viewport)
- handlePan(dx, dy)
- handleZoom(scale, center)
- addStroke(stroke)
- **Depends on:**
_None_

### StateManager
- **Responsibilities:**
- Global application state
- Local storage persistence
- Offline queue management
- **Interfaces:**
- getState()
- setState(partialState)
- subscribe(listener)
- persistLocally(key, data)
- queueOfflineAction(action)
- **Depends on:**
_None_

### DatabaseModule
- **Responsibilities:**
- PostgreSQL connection management
- Query execution
- Transaction handling
- Migration management
- **Interfaces:**
- query(sql, params)
- transaction(callback)
- migrate()
- **Depends on:**
_None_

## 🗃 Data Model Notes
- Users table: id (UUID PK), email (unique), username, password_hash (bcrypt), created_at, updated_at
- CanvasEntries table: id (UUID PK), user_id (UUID FK), title (text), encrypted_data (text), version (int), created_at, updated_at
- Indexes: user_id on CanvasEntries for faster lookups, email on Users for login queries
- Encrypted data format: base64 encoded JSON containing {strokes: [], viewport: {}, metadata: {}}

## 🔐 Validation & Security
- Password minimum 8 characters with bcrypt hashing (cost factor 12)
- JWT tokens expire after 7 days, stored in localStorage with httpOnly cookies
- All API routes except /auth/register and /auth/login require valid JWT
- Encrypted data validated as base64 before storage
- Rate limiting: 100 requests per minute per IP address
- CORS restricted to origin domain
- SQL injection prevention via parameterized queries
- Input sanitization for all user-generated content

## 🧯 Error Handling Strategy
Global error boundary in React captures component errors. API errors return JSON with {error: string, code: string}. Client-side retries exponential backoff for failed sync operations. Version conflicts trigger automatic merge prompt.

## 🔭 Observability
- **Logging:** Structured JSON logging on backend with levels: error, warn, info, debug. Client-side errors logged to console with remote error reporting service.
- **Tracing:** Request ID injection for API calls to trace sync operations from client to database
- **Metrics:**
- API response times (p50, p95, p99)
- Canvas render FPS
- Sync success/failure rates
- Active user count
- Canvas entry counts
- Error rates by endpoint

## ⚡ Performance Notes
- Canvas rendering uses requestAnimationFrame for 60fps target
- Stroke batching: Group strokes rendered within same frame
- Virtualization: Only render visible viewport strokes when count exceeds 1000
- Local storage: IndexedDB for canvas data, localStorage for auth tokens
- Connection pooling: PostgreSQL pool size 20, max 50
- Compression: Gzip compression for API responses with encrypted payloads
- Debouncing: 300ms debounce for canvas auto-save during editing

## 🧪 Testing Strategy
### Unit
- Auth module: password hashing, token generation/validation
- Encryption module: encrypt/decrypt with known vectors
- Canvas renderer: stroke rendering, viewport calculations
- State manager: Context updates, local storage persistence
### Integration
- API endpoints: CRUD operations with test database
- Sync service: conflict resolution scenarios
- Database: migration rollback/reapply
### E2E
- User registration and login flow
- Canvas creation, editing, and sync
- Offline editing and online sync
- Version conflict resolution

## 🚀 Rollout Plan
- Phase 1: Deploy backend API with authentication and canvas CRUD endpoints
- Phase 2: Deploy frontend with local storage and offline editing
- Phase 3: Enable sync service for cloud backup
- Phase 4: Performance optimization and monitoring integration

## ❓ Open Questions
- What is the maximum encrypted payload size for canvas entries?
- Should we implement soft deletes for canvas entries?
- What is the retention policy for deleted user data?
