# Agent Prompts — Werkstatt Infinite

## 🧭 Global Rules

### ✅ Do
- Use React with Tailwind CSS for frontend
- Use Node.js with Express for REST API backend
- Use PostgreSQL for all data persistence
- Implement client-side encryption using Web Crypto API
- Use IndexedDB for local canvas storage
- Use localStorage for auth token persistence
- Implement JWT authentication with httpOnly cookies
- Follow the exact acceptance criteria from each task
- Use TypeScript throughout the project
- Test all functionality with Jest and Supertest

### ❌ Don’t
- Do not replace React with any other frontend framework
- Do not replace PostgreSQL with any other database
- Do not introduce alternative authentication methods
- Do not use external encryption libraries - Web Crypto API only
- Do not create backend API if Tauri is specified
- Do not invent ports - use defaults (3000 for frontend, 8000 for backend)
- Do not add dependencies without marking as OPTIONAL
- Do not modify acceptance criteria or task requirements

## 🧩 Task Prompts
## Set up project structure and dependencies

**Context**
Initialize the full-stack project with React frontend, Express backend, and all required dependencies. Configure environment variables and Git repository.

### Universal Agent Prompt
```
ROLE: Expert Full Stack Engineer

GOAL: Scaffold React + Express + PostgreSQL project with all dependencies

CONTEXT: Initialize the full-stack project with React frontend, Express backend, and all required dependencies. Configure environment variables and Git repository.

FILES TO CREATE:
- backend/package.json
- backend/.env.example
- backend/.gitignore
- frontend/package.json
- frontend/.env.example
- frontend/.gitignore
- .gitignore
- README.md

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create backend directory and initialize package.json with: express, pg, bcrypt, jsonwebtoken, cors, express-rate-limit, compression, dotenv, typescript, @types/*, ts-node, nodemon, jest, @types/jest, supertest
2. Create frontend directory with React using Vite or CRA, install: tailwindcss, react-router-dom, axios, zustand or context (state management)
3. Create backend/.env.example with: DATABASE_URL, JWT_SECRET, PORT=8000, CORS_ORIGIN, NODE_ENV
4. Create frontend/.env.example with: VITE_API_URL or REACT_APP_API_URL
5. Configure TypeScript for both backend and frontend
6. Create root .gitignore excluding node_modules, .env, dist, build
7. Initialize git repository
8. Create basic README with project overview and setup instructions

VALIDATION:
cd backend && npm install && cd ../frontend && npm install
```

---

## Design and create PostgreSQL database schema

**Context**
Create the users table with proper indexes, migration scripts, and connection pooling configuration.

### Universal Agent Prompt
```
ROLE: Database Engineer

GOAL: Create users table with migration script and connection pool

CONTEXT: Create the users table with proper indexes, migration scripts, and connection pooling configuration.

FILES TO CREATE:
- backend/db/migrations/001_create_users_table.sql
- backend/db/migrations/rollback/001_create_users_table.sql
- backend/db/config.ts
- backend/db/index.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create migration file 001_create_users_table.sql with users table: id SERIAL PRIMARY KEY, email VARCHAR(255) UNIQUE NOT NULL, username VARCHAR(100) NOT NULL, password_hash TEXT NOT NULL, created_at TIMESTAMP DEFAULT NOW(), updated_at TIMESTAMP DEFAULT NOW()
2. Create unique index on email column
3. Create rollback migration to drop users table
4. Create db/config.ts with connection pool configuration (min: 20, max: 50, timeout: 30000)
5. Create db/index.ts that exports pool and connection helper
6. Add migration runner script in package.json

VALIDATION:
cd backend && npm run db:migrate
```

---

## Implement password hashing utilities

**Context**
Create bcrypt wrapper with cost factor 12 and minimum 8 character password validation.

### Universal Agent Prompt
```
ROLE: Backend Security Engineer

GOAL: Create password hashing utility with bcrypt

CONTEXT: Create bcrypt wrapper with cost factor 12 and minimum 8 character password validation.

FILES TO CREATE:
- backend/src/utils/password.ts
- backend/src/utils/__tests__/password.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create password.ts with hash(password) function that validates min 8 chars, uses bcrypt with cost factor 12
2. Create compare(password, hash) function that returns boolean
3. Throw error for passwords under 8 characters
4. Create unit tests for hash function with valid/invalid inputs
5. Create unit tests for compare function with matching/non-matching passwords

VALIDATION:
cd backend && npm test -- password.test.ts
```

---

## Implement JWT token generation and validation

**Context**
Create JWT service with 7-day expiration using secret from environment variable.

### Universal Agent Prompt
```
ROLE: Backend Security Engineer

GOAL: Create JWT service with 7-day token expiration

CONTEXT: Create JWT service with 7-day expiration using secret from environment variable.

FILES TO CREATE:
- backend/src/utils/jwt.ts
- backend/src/utils/__tests__/jwt.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create jwt.ts with generateToken(user) function that creates JWT with user_id and email in payload, expires in 7 days
2. Use JWT_SECRET from environment
3. Create validateToken(token) function that decodes and returns payload, throws error on invalid/expired
4. Create unit tests for generateToken
5. Create unit tests for validateToken with valid and invalid tokens

VALIDATION:
cd backend && npm test -- jwt.test.ts
```

---

## Create POST /api/auth/register endpoint

**Context**
Implement user registration with email validation, duplicate checking, and password hashing.

### Universal Agent Prompt
```
ROLE: Backend API Engineer

GOAL: Implement registration endpoint with validation and auth

CONTEXT: Implement user registration with email validation, duplicate checking, and password hashing.

FILES TO CREATE:
- backend/src/routes/auth.ts
- backend/src/controllers/authController.ts
- backend/src/middleware/validation.ts
- backend/src/tests/auth.test.ts

FILES TO MODIFY:
- backend/src/index.ts

DETAILED STEPS:
1. Create validation middleware for email format using regex
2. Create authController.register controller: accepts {email, password, username}, validates email format, checks for existing email (return 409), hashes password with bcrypt, creates user record, returns {user, token}
3. Create auth routes with POST /api/auth/register
4. Add auth routes to main app in index.ts
5. Create integration tests covering: valid registration, invalid email, duplicate email, missing fields

VALIDATION:
cd backend && npm test -- auth.test.ts
```

---

## Create POST /api/auth/login endpoint

**Context**
Implement user login with credential verification and token generation.

### Universal Agent Prompt
```
ROLE: Backend API Engineer

GOAL: Implement login endpoint with credential verification

CONTEXT: Implement user login with credential verification and token generation.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/src/controllers/authController.ts
- backend/src/routes/auth.ts
- backend/src/tests/auth.test.ts

DETAILED STEPS:
1. Add authController.login controller: accepts {email, password}, finds user by email, compares password with bcrypt.compare, returns 401 for invalid credentials, returns {user, token} on success
2. Add POST /api/auth/login route
3. Add integration tests for valid login, invalid email, invalid password

VALIDATION:
cd backend && npm test -- auth.test.ts
```

---

## Implement JWT authentication middleware

**Context**
Create Express middleware to validate Bearer tokens on protected routes.

### Universal Agent Prompt
```
ROLE: Backend Security Engineer

GOAL: Create JWT authentication middleware for Express

CONTEXT: Create Express middleware to validate Bearer tokens on protected routes.

FILES TO CREATE:
- backend/src/middleware/auth.ts
- backend/src/middleware/__tests__/auth.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create auth middleware that extracts Bearer token from Authorization header
2. Return 401 if token missing or invalid
3. Validate token using jwt.ts validateToken
4. Add decoded user object to req.user
5. Create unit tests for valid token, missing token, invalid token

VALIDATION:
cd backend && npm test -- auth.test.ts
```

---

## Create CanvasEntries database table

**Context**
Create table for canvas storage with user foreign key and proper indexes.

### Universal Agent Prompt
```
ROLE: Database Engineer

GOAL: Create canvas entries table with foreign key and indexes

CONTEXT: Create table for canvas storage with user foreign key and proper indexes.

FILES TO CREATE:
- backend/db/migrations/002_create_canvas_entries_table.sql
- backend/db/migrations/rollback/002_create_canvas_entries_table.sql

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create migration with canvas_entries table: id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES users(id) ON DELETE CASCADE, title VARCHAR(255) NOT NULL, encrypted_data TEXT NOT NULL, version INTEGER NOT NULL DEFAULT 1, created_at TIMESTAMP DEFAULT NOW(), updated_at TIMESTAMP DEFAULT NOW()
2. Add index on user_id column for query optimization
3. Create rollback migration
4. Test migration and rollback

VALIDATION:
cd backend && npm run db:migrate
```

---

## Implement base64 validation utility

**Context**
Create validator for encrypted data base64 format.

### Universal Agent Prompt
```
ROLE: Backend Utilities Engineer

GOAL: Create base64 validation utility

CONTEXT: Create validator for encrypted data base64 format.

FILES TO CREATE:
- backend/src/utils/validation.ts
- backend/src/utils/__tests__/validation.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create isValidBase64(str) function that returns boolean for valid base64 strings
2. Reject malformed base64 strings
3. Create unit tests with valid and invalid base64 inputs

VALIDATION:
cd backend && npm test -- validation.test.ts
```

---

## Create POST /api/canvas endpoint

**Context**
Implement canvas creation with encrypted data storage and authentication.

### Universal Agent Prompt
```
ROLE: Backend API Engineer

GOAL: Implement canvas creation endpoint with auth

CONTEXT: Implement canvas creation with encrypted data storage and authentication.

FILES TO CREATE:
- backend/src/routes/canvas.ts
- backend/src/controllers/canvasController.ts
- backend/src/tests/canvas.test.ts

FILES TO MODIFY:
- backend/src/index.ts

DETAILED STEPS:
1. Create canvasController.create controller: requires auth middleware, accepts {title, encrypted_data, version, created_at}, validates base64 format (return 400), inserts canvas entry with user_id, returns canvas object with generated id
2. Add POST /api/canvas route with auth middleware
3. Create integration tests: unauthorized, invalid base64, valid creation

VALIDATION:
cd backend && npm test -- canvas.test.ts
```

---

## Create GET /api/canvas/:id endpoint

**Context**
Implement canvas retrieval with ownership verification.

### Universal Agent Prompt
```
ROLE: Backend API Engineer

GOAL: Implement canvas get endpoint with ownership check

CONTEXT: Implement canvas retrieval with ownership verification.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/src/controllers/canvasController.ts
- backend/src/routes/canvas.ts
- backend/src/tests/canvas.test.ts

DETAILED STEPS:
1. Add canvasController.getById controller: requires auth, queries canvas by id and user_id, returns 404 if not found or wrong user, returns full canvas object on success
2. Add GET /api/canvas/:id route
3. Add integration tests for ownership checks

VALIDATION:
cd backend && npm test -- canvas.test.ts
```

---

## Create PUT /api/canvas/:id endpoint

**Context**
Implement canvas update with version conflict detection.

### Universal Agent Prompt
```
ROLE: Backend API Engineer

GOAL: Implement canvas update with version conflict detection

CONTEXT: Implement canvas update with version conflict detection.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/src/controllers/canvasController.ts
- backend/src/routes/canvas.ts
- backend/src/tests/canvas.test.ts

DETAILED STEPS:
1. Add canvasController.update controller: requires auth, accepts {title, encrypted_data, version}, checks ownership, compares version with current version (return 409 conflict if mismatch), updates canvas and increments version, returns updated canvas
2. Add PUT /api/canvas/:id route
3. Add integration tests for version conflicts

VALIDATION:
cd backend && npm test -- canvas.test.ts
```

---

## Create DELETE /api/canvas/:id endpoint

**Context**
Implement canvas deletion with ownership verification.

### Universal Agent Prompt
```
ROLE: Backend API Engineer

GOAL: Implement canvas delete endpoint

CONTEXT: Implement canvas deletion with ownership verification.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/src/controllers/canvasController.ts
- backend/src/routes/canvas.ts
- backend/src/tests/canvas.test.ts

DETAILED STEPS:
1. Add canvasController.delete controller: requires auth, checks ownership, deletes canvas, returns 404 if not found, returns {id, deleted: true} on success
2. Add DELETE /api/canvas/:id route
3. Add integration tests for deletion

VALIDATION:
cd backend && npm test -- canvas.test.ts
```

---

## Create GET /api/canvas list endpoint

**Context**
Implement paginated canvas listing for authenticated user.

### Universal Agent Prompt
```
ROLE: Backend API Engineer

GOAL: Implement paginated canvas list endpoint

CONTEXT: Implement paginated canvas listing for authenticated user.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/src/controllers/canvasController.ts
- backend/src/routes/canvas.ts
- backend/src/tests/canvas.test.ts

DETAILED STEPS:
1. Add canvasController.list controller: requires auth, queries canvases by user_id with pagination (limit, offset), returns {canvases: [{id, title, created_at, updated_at}], total: count}
2. Add GET /api/canvas route
3. Add integration tests for pagination

VALIDATION:
cd backend && npm test -- canvas.test.ts
```

---

## Implement key derivation from password

**Context**
Create PBKDF2-based key derivation with salt generation using Web Crypto API.

### Universal Agent Prompt
```
ROLE: Frontend Cryptography Engineer

GOAL: Create PBKDF2 key derivation using Web Crypto API

CONTEXT: Create PBKDF2-based key derivation with salt generation using Web Crypto API.

FILES TO CREATE:
- frontend/src/lib/crypto/keyDerivation.ts
- frontend/src/lib/crypto/__tests__/keyDerivation.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create deriveKey(password, salt) async function using Web Crypto API PBKDF2
2. Generate random salt if not provided (crypto.getRandomValues)
3. Use appropriate iteration count (100,000+)
4. Return CryptoKey object
5. Create unit tests with known vectors

VALIDATION:
cd frontend && npm test -- keyDerivation.test.ts
```

---

## Implement AES-GCM encryption

**Context**
Create encrypt function using Web Crypto API with IV and auth tag.

### Universal Agent Prompt
```
ROLE: Frontend Cryptography Engineer

GOAL: Create AES-GCM encryption using Web Crypto API

CONTEXT: Create encrypt function using Web Crypto API with IV and auth tag.

FILES TO CREATE:
- frontend/src/lib/crypto/encryption.ts
- frontend/src/lib/crypto/__tests__/encryption.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create encrypt(data, key) async function using AES-GCM
2. Generate random IV (12 bytes)
3. Encrypt data with key
4. Combine IV + ciphertext + auth tag into single output
5. Return base64 encoded string
6. Create unit tests with known vectors

VALIDATION:
cd frontend && npm test -- encryption.test.ts
```

---

## Implement AES-GCM decryption

**Context**
Create decrypt function that extracts IV, validates auth tag, returns data.

### Universal Agent Prompt
```
ROLE: Frontend Cryptography Engineer

GOAL: Create AES-GCM decryption using Web Crypto API

CONTEXT: Create decrypt function that extracts IV, validates auth tag, returns data.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- frontend/src/lib/crypto/encryption.ts
- frontend/src/lib/crypto/__tests__/encryption.test.ts

DETAILED STEPS:
1. Create decrypt(encryptedData, key) async function
2. Parse base64 input to extract IV, ciphertext, auth tag
3. Decrypt using AES-GCM
4. Throw error for invalid auth tag
5. Throw error for malformed input
6. Return original data
7. Create unit tests with known vectors

VALIDATION:
cd frontend && npm test -- encryption.test.ts
```

---

## Create encryption service module

**Context**
Package encryption utilities into reusable React context/service with TypeScript types.

### Universal Agent Prompt
```
ROLE: React Frontend Engineer

GOAL: Create encryption service React context with TypeScript

CONTEXT: Package encryption utilities into reusable React context/service with TypeScript types.

FILES TO CREATE:
- frontend/src/lib/crypto/types.ts
- frontend/src/contexts/EncryptionContext.tsx
- frontend/src/lib/crypto/README.md

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create types.ts with TypeScript interfaces for all crypto functions
2. Create EncryptionContext with Provider component
3. Export useEncryption hook with encrypt, decrypt, deriveKey functions
4. Add error handling for crypto failures
5. Create README.md documenting usage

VALIDATION:
cd frontend && npm run build
```

---

## Set up Canvas API context and event handlers

**Context**
Initialize HTML5 Canvas, handle resize events, set up basic render loop.

### Universal Agent Prompt
```
ROLE: React Canvas Engineer

GOAL: Set up HTML5 Canvas with render loop and resize handling

CONTEXT: Initialize HTML5 Canvas, handle resize events, set up basic render loop.

FILES TO CREATE:
- frontend/src/components/canvas/CanvasRenderer.tsx

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create CanvasRenderer React component using useRef for canvas element
2. Initialize Canvas 2D context on mount
3. Handle window resize events to update canvas dimensions
4. Set up requestAnimationFrame render loop
5. Implement cleanup on unmount (cancelAnimationFrame)
6. Canvas should fill entire viewport

VALIDATION:
cd frontend && npm run build
```

---

## Implement viewport pan functionality

**Context**
Add mouse/touch drag to pan canvas viewport.

### Universal Agent Prompt
```
ROLE: React Canvas Engineer

GOAL: Implement viewport pan with mouse/touch drag

CONTEXT: Add mouse/touch drag to pan canvas viewport.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- frontend/src/components/canvas/CanvasRenderer.tsx

DETAILED STEPS:
1. Add viewport offset state {x, y}
2. Add mouse/touch event handlers for drag: onMouseDown, onMouseMove, onMouseUp, onTouchStart, onTouchMove, onTouchEnd
3. Update viewport offset during drag
4. Apply offset in render function
5. Ensure smooth 60fps panning

VALIDATION:
cd frontend && npm run build
```

---

## Implement viewport zoom functionality

**Context**
Add wheel/pinch to zoom centered on cursor or gesture.

### Universal Agent Prompt
```
ROLE: React Canvas Engineer

GOAL: Implement viewport zoom with wheel/pinch gestures

CONTEXT: Add wheel/pinch to zoom centered on cursor or gesture.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- frontend/src/components/canvas/CanvasRenderer.tsx

DETAILED STEPS:
1. Add zoom level state
2. Add onWheel handler for zoom at cursor position
3. Add touch handlers for pinch zoom
4. Clamp zoom to min/max bounds
5. Calculate offset to keep cursor position stable during zoom
6. Apply zoom transform in render function

VALIDATION:
cd frontend && npm run build
```

---

## Create Bauhaus snap-to-grid system

**Context**
Implement visible grid with snap points for strokes.

### Universal Agent Prompt
```
ROLE: React Canvas Engineer

GOAL: Create snap-to-grid system with visible grid lines

CONTEXT: Implement visible grid with snap points for strokes.

FILES TO CREATE:
- frontend/src/components/canvas/GridSystem.tsx
- frontend/src/hooks/useGridSnap.ts

FILES TO MODIFY:
- frontend/src/components/canvas/CanvasRenderer.tsx

DETAILED STEPS:
1. Create grid system with configurable interval
2. Render grid lines in canvas, scaled with zoom
3. Create useGridSnap hook that snaps coordinates to grid intersections
4. Add toggle state for enabling/disabling snap
5. Integrate grid rendering into CanvasRenderer

VALIDATION:
cd frontend && npm run build
```

---

## Implement stroke drawing system

**Context**
Capture mouse/touch strokes, render as canvas paths.

### Universal Agent Prompt
```
ROLE: React Canvas Engineer

GOAL: Implement stroke drawing with mouse/touch

CONTEXT: Capture mouse/touch strokes, render as canvas paths.

FILES TO CREATE:
- frontend/src/types/canvas.ts
- frontend/src/hooks/useStrokeDrawing.ts

FILES TO MODIFY:
- frontend/src/components/canvas/CanvasRenderer.tsx

DETAILED STEPS:
1. Define Stroke data structure: {id, points: [{x, y}], color, width}
2. Create useStrokeDrawing hook with state for current stroke
3. Add event handlers: mouse down starts stroke, move adds points, up finalizes stroke
4. Render strokes as paths in canvas
5. Integrate with grid snap for point coordinates

VALIDATION:
cd frontend && npm run build
```

---

## Implement stroke batching optimization

**Context**
Group strokes within same frame for efficient rendering.

### Universal Agent Prompt
```
ROLE: React Canvas Engineer

GOAL: Implement stroke batching for 60fps rendering

CONTEXT: Group strokes within same frame for efficient rendering.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- frontend/src/components/canvas/CanvasRenderer.tsx
- frontend/src/hooks/useStrokeDrawing.ts

DETAILED STEPS:
1. Add stroke batch state array
2. Batch multiple strokes created in same frame
3. Optimize render to handle 100+ strokes at 60fps
4. Implement clear function to redraw all strokes

VALIDATION:
cd frontend && npm run build
```

---

## Add stroke viewport virtualization

**Context**
Only render strokes within or intersecting visible viewport.

### Universal Agent Prompt
```
ROLE: React Canvas Engineer

GOAL: Implement viewport virtualization for strokes

CONTEXT: Only render strokes within or intersecting visible viewport.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- frontend/src/components/canvas/CanvasRenderer.tsx

DETAILED STEPS:
1. Calculate viewport bounds each frame based on offset and zoom
2. Filter strokes to only those within or intersecting bounds
3. Skip rendering for strokes outside viewport
4. Test with 1000+ strokes
5. Ensure no visible flicker on pan/zoom

VALIDATION:
cd frontend && npm run build
```

---

## Design and create global state structure

**Context**
Define state shape for user, canvases, current canvas, sync status.

### Universal Agent Prompt
```
ROLE: React State Management Engineer

GOAL: Define TypeScript interfaces for global state

CONTEXT: Define state shape for user, canvases, current canvas, sync status.

FILES TO CREATE:
- frontend/src/types/state.ts
- frontend/src/store/types.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create State interface with: user, canvases, currentCanvas, syncStatus, appSettings
2. Define User type: id, email, username, token
3. Define Canvas type: id, title, encryptedData, version, createdAt, updatedAt
4. Define SyncStatus type: 'idle' | 'syncing' | 'error' | 'offline'
5. Define state update action types

VALIDATION:
cd frontend && npm run build
```

---

## Implement React Context for state management

**Context**
Create StateContext and Provider with getState, setState, subscribe.

### Universal Agent Prompt
```
ROLE: React State Management Engineer

GOAL: Create React Context for global state management

CONTEXT: Create StateContext and Provider with getState, setState, subscribe.

FILES TO CREATE:
- frontend/src/contexts/StateContext.tsx
- frontend/src/contexts/__tests__/StateContext.test.tsx

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create StateContext with initial state
2. Create StateProvider component with children
3. Implement getState function returning current state
4. Implement setState performing shallow merge
5. Implement subscribe registering listener callbacks
6. Export useStateContext hook
7. Create unit tests for state updates

VALIDATION:
cd frontend && npm test -- StateContext.test.tsx
```

---

## Implement localStorage persistence for auth

**Context**
Save and load JWT tokens from browser localStorage.

### Universal Agent Prompt
```
ROLE: React Frontend Engineer

GOAL: Implement localStorage JWT persistence

CONTEXT: Save and load JWT tokens from browser localStorage.

FILES TO CREATE:
- frontend/src/lib/storage/authStorage.ts

FILES TO MODIFY:
- frontend/src/contexts/StateContext.tsx

DETAILED STEPS:
1. Create authStorage module with saveToken(token), loadToken(), clearToken() functions
2. Integrate with StateContext to save token on login
3. Load token on app init
4. Clear token on logout
5. Add try/catch for corrupted data handling

VALIDATION:
cd frontend && npm run build
```

---

## Implement IndexedDB for canvas data

**Context**
Create IndexedDB wrapper for storing encrypted canvas entries.

### Universal Agent Prompt
```
ROLE: React Storage Engineer

GOAL: Create IndexedDB wrapper for canvas storage

CONTEXT: Create IndexedDB wrapper for storing encrypted canvas entries.

FILES TO CREATE:
- frontend/src/lib/storage/indexedDB.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create openDatabase() function opening 'WerkstattCanvasDB' with version 1
2. Create object store 'canvases' with keyPath 'id'
3. Create saveCanvas(canvas) function
4. Create getCanvas(id) function
5. Create getAllCanvases() function
6. Create deleteCanvas(id) function
7. Handle quota exceeded errors gracefully

VALIDATION:
cd frontend && npm run build
```

---

## Create offline action queue

**Context**
Queue API actions when offline, replay when online.

### Universal Agent Prompt
```
ROLE: React Offline Engineer

GOAL: Implement offline action queue with persistence

CONTEXT: Queue API actions when offline, replay when online.

FILES TO CREATE:
- frontend/src/lib/sync/actionQueue.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create action queue array stored in localStorage
2. Define action type: {type, payload, timestamp, retries}
3. Create enqueue(action) function
4. Create replayQueue() function that processes queued actions
5. Failed actions increment retries and remain in queue
6. Clear queue on successful replay

VALIDATION:
cd frontend && npm run build
```

---

## Implement online/offline detection

**Context**
Listen to online/offline events, update app state.

### Universal Agent Prompt
```
ROLE: React Offline Engineer

GOAL: Implement online/offline event detection

CONTEXT: Listen to online/offline events, update app state.

FILES TO CREATE:
- frontend/src/hooks/useOnlineStatus.ts

FILES TO MODIFY:
- frontend/src/contexts/StateContext.tsx

DETAILED STEPS:
1. Create useOnlineStatus hook listening to window online/offline events
2. Update state on online/offline events
3. Show visual indicator to user
4. Trigger queue replay on online event
5. Trigger queue reset on offline event

VALIDATION:
cd frontend && npm run build
```

---

## Implement change tracking for local edits

**Context**
Track all canvas modifications with timestamps and action types.

### Universal Agent Prompt
```
ROLE: React Sync Engineer

GOAL: Implement local change tracking for sync

CONTEXT: Track all canvas modifications with timestamps and action types.

FILES TO CREATE:
- frontend/src/lib/sync/changeTracker.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create change tracking structure: {canvasId, action, timestamp, data}
2. Define action types: 'create', 'update', 'delete'
3. Create trackChange(change) function
4. Create getChangesSince(timestamp) function
5. Create clearChanges() function
6. Store changes in localStorage

VALIDATION:
cd frontend && npm run build
```

---

## Create POST /api/sync endpoint

**Context**
Backend sync endpoint with last_sync parameter and change delta processing.

### Universal Agent Prompt
```
ROLE: Backend API Engineer

GOAL: Implement sync endpoint with change processing

CONTEXT: Backend sync endpoint with last_sync parameter and change delta processing.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/src/routes/sync.ts
- backend/src/controllers/syncController.ts
- backend/src/tests/sync.test.ts

DETAILED STEPS:
1. Create syncController.sync controller: requires auth, accepts {last_sync, local_changes}
2. Query server changes since last_sync for user
3. Process local_changes and apply to database
4. Return {server_changes: [], sync_time: timestamp}
5. Return 400 for invalid format
6. Create integration tests for sync scenarios

VALIDATION:
cd backend && npm test -- sync.test.ts
```

---

## Implement client sync initiator

**Context**
Client function to collect local changes and call sync API.

### Universal Agent Prompt
```
ROLE: React Sync Engineer

GOAL: Implement client sync initiator function

CONTEXT: Client function to collect local changes and call sync API.

FILES TO CREATE:
- frontend/src/lib/sync/syncClient.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create initiateSync() async function
2. Collect local changes since last_sync timestamp
3. Format sync request payload
4. Call POST /api/sync
5. Process server response and apply server_changes
6. Update last_sync timestamp
7. Handle network errors with retry logic

VALIDATION:
cd frontend && npm run build
```

---

## Implement version conflict detection

**Context**
Detect when server version differs from local version during sync.

### Universal Agent Prompt
```
ROLE: React Sync Engineer

GOAL: Implement version conflict detection

CONTEXT: Detect when server version differs from local version during sync.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- frontend/src/lib/sync/syncClient.ts

DETAILED STEPS:
1. Add version comparison logic in sync response processing
2. Compare local version vs server version for each canvas
3. Flag conflicts where versions differ
4. Return conflict metadata: {canvasId, localVersion, serverVersion}
5. Create unit tests for conflict scenarios

VALIDATION:
cd frontend && npm test -- syncClient.test.ts
```

---

## Create conflict resolution UI

**Context**
Prompt user to resolve conflicts with local/server/merge options.

### Universal Agent Prompt
```
ROLE: React UI Engineer

GOAL: Create conflict resolution modal UI

CONTEXT: Prompt user to resolve conflicts with local/server/merge options.

FILES TO CREATE:
- frontend/src/components/conflict/ConflictModal.tsx
- frontend/src/hooks/useConflictResolution.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create ConflictModal component showing conflict details
2. Display options: Keep Local, Keep Server, Cancel
3. Create useConflictResolution hook
4. Handle user selection and send to API
5. Update state after resolution
6. Style with Tailwind CSS

VALIDATION:
cd frontend && npm run build
```

---

## Implement automatic sync on interval

**Context**
Periodically trigger sync when online, with debounce on edits.

### Universal Agent Prompt
```
ROLE: React Sync Engineer

GOAL: Implement automatic sync with interval and debounce

CONTEXT: Periodically trigger sync when online, with debounce on edits.

FILES TO CREATE:
- frontend/src/hooks/useAutoSync.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create useAutoSync hook with useEffect
2. Trigger sync every 30 seconds when online
3. Debounce sync for 5 seconds after edits
4. Skip sync when offline
5. Add manual sync trigger function
6. Show sync status indicator

VALIDATION:
cd frontend && npm run build
```

---

## Create auto-save debouncer

**Context**
Implement 300ms debounce function for canvas save operations.

### Universal Agent Prompt
```
ROLE: React Utilities Engineer

GOAL: Create debounce utility for auto-save

CONTEXT: Implement 300ms debounce function for canvas save operations.

FILES TO CREATE:
- frontend/src/utils/debounce.ts
- frontend/src/utils/__tests__/debounce.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create debounce(fn, delay) function that delays execution
2. Subsequent calls reset timer
3. Execute after 300ms of inactivity
4. Return cancel function
5. Create unit tests for debounce behavior

VALIDATION:
cd frontend && npm test -- debounce.test.ts
```

---

## Integrate auto-save with stroke changes

**Context**
Trigger auto-save on stroke completion (mouse up).

### Universal Agent Prompt
```
ROLE: React Canvas Engineer

GOAL: Integrate auto-save with stroke drawing

CONTEXT: Trigger auto-save on stroke completion (mouse up).

FILES TO CREATE:
_None_

FILES TO MODIFY:
- frontend/src/hooks/useStrokeDrawing.ts
- frontend/src/hooks/useAutoSave.ts

DETAILED STEPS:
1. Create useAutoSave hook using debounce utility
2. Trigger save on each stroke end (mouse up)
3. Multiple rapid strokes result in single save
4. Save includes all stroke data encrypted
5. Show success/failure indicator to user

VALIDATION:
cd frontend && npm run build
```

---

## Add save status indicator

**Context**
Visual indicator for unsaved changes, saving, and saved states.

### Universal Agent Prompt
```
ROLE: React UI Engineer

GOAL: Create save status indicator component

CONTEXT: Visual indicator for unsaved changes, saving, and saved states.

FILES TO CREATE:
- frontend/src/components/ui/SaveStatusIndicator.tsx

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create SaveStatusIndicator component
2. States: 'unsaved', 'saving', 'saved', 'error'
3. Show appropriate message and icon for each state
4. Integrate with auto-save hook
5. Style with Tailwind CSS

VALIDATION:
cd frontend && npm run build
```

---

## Add email format validation

**Context**
Server-side email validation using regex.

### Universal Agent Prompt
```
ROLE: Backend Validation Engineer

GOAL: Add server-side email validation

CONTEXT: Server-side email validation using regex.

FILES TO CREATE:
- backend/src/utils/validators.ts
- backend/src/utils/__tests__/validators.test.ts

FILES TO MODIFY:
- backend/src/controllers/authController.ts

DETAILED STEPS:
1. Create isValidEmail(email) function using regex
2. Returns boolean for valid email format
3. Integrate into register endpoint validation
4. Return 400 for invalid format
5. Create unit tests for valid/invalid emails

VALIDATION:
cd backend && npm test -- validators.test.ts
```

---

## Implement rate limiting middleware

**Context**
100 requests per minute per IP using express-rate-limit.

### Universal Agent Prompt
```
ROLE: Backend Security Engineer

GOAL: Implement rate limiting middleware

CONTEXT: 100 requests per minute per IP using express-rate-limit.

FILES TO CREATE:
- backend/src/middleware/rateLimit.ts

FILES TO MODIFY:
- backend/src/index.ts

DETAILED STEPS:
1. Create rate limit middleware using express-rate-limit
2. Set 100 requests per minute per IP
3. Return 429 when limit exceeded with reset time
4. Make limit configurable via environment variable
5. Apply to all API routes

VALIDATION:
cd backend && npm run build
```

---

## Configure CORS restrictions

**Context**
Restrict CORS to origin domain.

### Universal Agent Prompt
```
ROLE: Backend Security Engineer

GOAL: Configure CORS for allowed origin

CONTEXT: Restrict CORS to origin domain.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/src/index.ts

DETAILED STEPS:
1. Configure CORS in Express using cors middleware
2. Set origin from environment variable CORS_ORIGIN
3. Allow credentials for cookies
4. Handle preflight requests correctly

VALIDATION:
cd backend && npm run build
```

---

## Add SQL injection prevention

**Context**
Ensure all database queries use parameterized queries.

### Universal Agent Prompt
```
ROLE: Backend Security Engineer

GOAL: Verify parameterized queries throughout codebase

CONTEXT: Ensure all database queries use parameterized queries.

FILES TO CREATE:
_None_

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Review all database queries in controllers
2. Ensure all queries use pg parameterization ($1, $2, etc.)
3. Verify no string concatenation in SQL queries
4. Document query safety in code review

VALIDATION:
cd backend && npm run build
```

---

## Implement input sanitization

**Context**
Sanitize all user-generated content.

### Universal Agent Prompt
```
ROLE: Backend Security Engineer

GOAL: Add input sanitization for user content

CONTEXT: Sanitize all user-generated content.

FILES TO CREATE:
- backend/src/utils/sanitize.ts

FILES TO MODIFY:
- backend/src/controllers/authController.ts
- backend/src/controllers/canvasController.ts

DETAILED STEPS:
1. Create sanitizeInput(input) function
2. Escape dangerous characters
3. Enforce length limits
4. Apply to title fields
5. Apply to username fields

VALIDATION:
cd backend && npm run build
```

---

## Add httpOnly cookie for JWT

**Context**
Store JWT in httpOnly cookie in addition to localStorage.

### Universal Agent Prompt
```
ROLE: Backend Security Engineer

GOAL: Add httpOnly cookie for JWT storage

CONTEXT: Store JWT in httpOnly cookie in addition to localStorage.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/src/controllers/authController.ts
- backend/src/middleware/auth.ts

DETAILED STEPS:
1. Set httpOnly cookie on login/register
2. Set cookie name from environment variable
3. Enable httpOnly flag
4. Enable secure flag in production
5. Clear cookie on logout
6. Update auth middleware to check cookie

VALIDATION:
cd backend && npm run build
```

---

## Create global error handler for Express

**Context**
Catch-all error handler with consistent JSON responses.

### Universal Agent Prompt
```
ROLE: Backend Error Handling Engineer

GOAL: Create global Express error handler

CONTEXT: Catch-all error handler with consistent JSON responses.

FILES TO CREATE:
- backend/src/middleware/errorHandler.ts

FILES TO MODIFY:
- backend/src/index.ts

DETAILED STEPS:
1. Create error handler middleware
2. Catch all errors with 4 parameters
3. Return consistent format: {error: string, code: string}
4. Set appropriate status codes
5. Log errors with context (timestamp, stack, request info)

VALIDATION:
cd backend && npm run build
```

---

## Implement React error boundary

**Context**
Error boundary component wrapping app, shows fallback UI.

### Universal Agent Prompt
```
ROLE: React Error Handling Engineer

GOAL: Create React error boundary component

CONTEXT: Error boundary component wrapping app, shows fallback UI.

FILES TO CREATE:
- frontend/src/components/error/ErrorBoundary.tsx

FILES TO MODIFY:
- frontend/src/App.tsx

DETAILED STEPS:
1. Create ErrorBoundary class component
2. Catch component errors in componentDidCatch
3. Show fallback UI with error details
4. Log errors to console
5. Add report button for remote logging (optional)
6. Wrap App component with ErrorBoundary

VALIDATION:
cd frontend && npm run build
```

---

## Add exponential backoff for API retries

**Context**
Client-side retry logic with exponential backoff for failed sync.

### Universal Agent Prompt
```
ROLE: React Error Handling Engineer

GOAL: Implement exponential backoff for retries

CONTEXT: Client-side retry logic with exponential backoff for failed sync.

FILES TO CREATE:
- frontend/src/utils/retry.ts

FILES TO MODIFY:
- frontend/src/lib/sync/syncClient.ts

DETAILED STEPS:
1. Create retryWithBackoff(asyncFn, maxRetries) function
2. Delay doubles each retry (1s, 2s, 4s, 8s)
3. Max 5 retries before giving up
4. Integrate into sync failures
5. Return result or throw final error

VALIDATION:
cd frontend && npm run build
```

---

## Create error reporting service

**Context**
Remote error reporting for client-side errors.

### Universal Agent Prompt
```
ROLE: React Observability Engineer

GOAL: Create client error reporting service

CONTEXT: Remote error reporting for client-side errors.

FILES TO CREATE:
- frontend/src/lib/error/reporting.ts

FILES TO MODIFY:
- frontend/src/components/error/ErrorBoundary.tsx

DETAILED STEPS:
1. Create reportError(error) function
2. Send to reporting endpoint
3. Include error stack, user agent, timestamp
4. Add opt-in/opt-out for privacy
5. Integrate with ErrorBoundary

VALIDATION:
cd frontend && npm run build
```

---

## Set up structured JSON logging

**Context**
Backend logging with error, warn, info, debug levels.

### Universal Agent Prompt
```
ROLE: Backend Observability Engineer

GOAL: Create structured JSON logging system

CONTEXT: Backend logging with error, warn, info, debug levels.

FILES TO CREATE:
- backend/src/utils/logger.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create logger with levels: error, warn, info, debug
2. Output JSON with timestamp, level, message
3. Configurable log level via LOG_LEVEL environment variable
4. Write logs to stdout
5. Use throughout backend

VALIDATION:
cd backend && npm run build
```

---

## Add request ID injection

**Context**
Generate unique request ID for each API call, log through stack.

### Universal Agent Prompt
```
ROLE: Backend Observability Engineer

GOAL: Add request ID middleware for tracing

CONTEXT: Generate unique request ID for each API call, log through stack.

FILES TO CREATE:
- backend/src/middleware/requestId.ts

FILES TO MODIFY:
- backend/src/index.ts
- backend/src/utils/logger.ts

DETAILED STEPS:
1. Create request ID middleware
2. Generate unique ID on each request (uuid or random)
3. Include request ID in all log statements
4. Return ID in X-Request-Id response header
5. Enable sync operation tracing

VALIDATION:
cd backend && npm run build
```

---

## Implement API response time metrics

**Context**
Track p50, p95, p99 response times per endpoint.

### Universal Agent Prompt
```
ROLE: Backend Observability Engineer

GOAL: Implement response time metrics tracking

CONTEXT: Track p50, p95, p99 response times per endpoint.

FILES TO CREATE:
- backend/src/middleware/metrics.ts
- backend/src/utils/metricsCollector.ts

FILES TO MODIFY:
- backend/src/index.ts

DETAILED STEPS:
1. Create metrics middleware measuring response time
2. Record time for each request by endpoint
3. Aggregate metrics and calculate percentiles
4. Store in memory with time window
5. Return metrics in JSON format

VALIDATION:
cd backend && npm run build
```

---

## Add canvas render FPS monitoring

**Context**
Track actual FPS during canvas rendering.

### Universal Agent Prompt
```
ROLE: React Performance Engineer

GOAL: Add FPS monitoring for canvas rendering

CONTEXT: Track actual FPS during canvas rendering.

FILES TO CREATE:
- frontend/src/hooks/useFPSMonitor.ts

FILES TO MODIFY:
- frontend/src/components/canvas/CanvasRenderer.tsx

DETAILED STEPS:
1. Create useFPSMonitor hook
2. Calculate FPS each second using requestAnimationFrame timestamps
3. Log to console in development mode
4. Show performance warning if below 50fps
5. Optional: send to metrics endpoint

VALIDATION:
cd frontend && npm run build
```

---

## Track sync success/failure rates

**Context**
Monitor sync operations and outcomes.

### Universal Agent Prompt
```
ROLE: React Observability Engineer

GOAL: Track sync operation metrics

CONTEXT: Monitor sync operations and outcomes.

FILES TO CREATE:
- frontend/src/lib/sync/syncMetrics.ts

FILES TO MODIFY:
- frontend/src/lib/sync/syncClient.ts

DETAILED STEPS:
1. Create sync metrics tracker
2. Increment success counter on good sync
3. Increment failure counter on bad sync
4. Calculate success rate percentage
5. Log metrics with context
6. Expose metrics for dashboard

VALIDATION:
cd frontend && npm run build
```

---

## Create metrics dashboard endpoint

**Context**
Admin endpoint returning aggregated metrics.

### Universal Agent Prompt
```
ROLE: Backend API Engineer

GOAL: Create admin metrics endpoint

CONTEXT: Admin endpoint returning aggregated metrics.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/src/routes/admin.ts
- backend/src/controllers/adminController.ts

DETAILED STEPS:
1. Create GET /api/metrics endpoint
2. Return JSON with: active user count, canvas entry counts, error rates by endpoint
3. Require admin authentication
4. Aggregate data from metrics collector
5. Apply to backend/src/index.ts routes

VALIDATION:
cd backend && npm run build
```

---

## Add Gzip compression for API responses

**Context**
Enable Gzip compression middleware in Express.

### Universal Agent Prompt
```
ROLE: Backend Performance Engineer

GOAL: Enable Gzip compression for API responses

CONTEXT: Enable Gzip compression middleware in Express.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/src/index.ts

DETAILED STEPS:
1. Add compression middleware to Express
2. Apply to all API responses
3. Configure compression threshold appropriately
4. Ensure encrypted payloads are compressed

VALIDATION:
cd backend && npm run build
```

---

## Optimize database queries with indexes

**Context**
Review and add indexes for common query patterns.

### Universal Agent Prompt
```
ROLE: Database Performance Engineer

GOAL: Add database indexes for query optimization

CONTEXT: Review and add indexes for common query patterns.

FILES TO CREATE:
- backend/db/migrations/003_add_performance_indexes.sql

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Review common query patterns in controllers
2. Run EXPLAIN ANALYZE on queries
3. Add indexes for foreign keys
4. Add indexes for frequently filtered columns
5. Verify query times under 50ms
6. Document index maintenance

VALIDATION:
cd backend && npm run db:migrate
```

---

## Implement requestAnimationFrame scheduling

**Context**
Ensure canvas rendering uses rAF for 60fps target.

### Universal Agent Prompt
```
ROLE: React Performance Engineer

GOAL: Ensure canvas uses requestAnimationFrame

CONTEXT: Ensure canvas rendering uses rAF for 60fps target.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- frontend/src/components/canvas/CanvasRenderer.tsx

DETAILED STEPS:
1. Verify all render calls in requestAnimationFrame callback
2. Coalesce multiple renders in single frame
3. Log frame time for monitoring
4. Implement graceful degradation if slow

VALIDATION:
cd frontend && npm run build
```

---

## Add connection pooling configuration

**Context**
Configure PostgreSQL pool sizes and timeouts.

### Universal Agent Prompt
```
ROLE: Database Performance Engineer

GOAL: Configure PostgreSQL connection pool

CONTEXT: Configure PostgreSQL pool sizes and timeouts.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/db/config.ts

DETAILED STEPS:
1. Set min pool size to 20
2. Set max pool size to 50
3. Configure connection timeout
4. Configure idle timeout
5. Add pool metrics logging

VALIDATION:
cd backend && npm run build
```

---

## Set up testing framework

**Context**
Configure Jest for unit tests, Supertest for API tests.

### Universal Agent Prompt
```
ROLE: Test Infrastructure Engineer

GOAL: Set up Jest and Supertest testing framework

CONTEXT: Configure Jest for unit tests, Supertest for API tests.

FILES TO CREATE:
- backend/jest.config.js
- frontend/jest.config.js
- backend/package.json
- frontend/package.json

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Configure Jest with TypeScript for backend
2. Configure Jest with TypeScript for frontend
3. Add test environment setup scripts
4. Enable coverage reporting
5. Configure Supertest for API tests
6. Add test scripts to package.json

VALIDATION:
cd backend && npm test -- --passWithNoTests
```

---

## Write unit tests for auth module

**Context**
Test password hashing, token generation, token validation.

### Universal Agent Prompt
```
ROLE: Backend Test Engineer

GOAL: Write unit tests for auth utilities

CONTEXT: Test password hashing, token generation, token validation.

FILES TO CREATE:
- backend/src/utils/__tests__/password.test.ts
- backend/src/utils/__tests__/jwt.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Write password hash tests with various inputs
2. Write token generation tests validating payload
3. Write token validation tests rejecting bad tokens
4. Ensure coverage above 90%

VALIDATION:
cd backend && npm test
```

---

## Write unit tests for encryption module

**Context**
Test encrypt/decrypt with known test vectors.

### Universal Agent Prompt
```
ROLE: Frontend Test Engineer

GOAL: Write unit tests for encryption module

CONTEXT: Test encrypt/decrypt with known test vectors.

FILES TO CREATE:
- frontend/src/lib/crypto/__tests__/encryption.test.ts
- frontend/src/lib/crypto/__tests__/keyDerivation.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Write tests that encrypt produces expected output
2. Write tests that decrypt reverses encrypt
3. Write tests for key derivation consistency
4. Write tests for invalid inputs throwing errors

VALIDATION:
cd frontend && npm test
```

---

## Write unit tests for canvas renderer

**Context**
Test stroke rendering, viewport calculations, virtualization.

### Universal Agent Prompt
```
ROLE: Frontend Test Engineer

GOAL: Write unit tests for canvas rendering utilities

CONTEXT: Test stroke rendering, viewport calculations, virtualization.

FILES TO CREATE:
- frontend/src/components/canvas/__tests__/CanvasRenderer.test.tsx

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Write tests for viewport offset calculations
2. Write tests for zoom calculations
3. Write tests for virtualization bounds
4. Write tests for stroke data structure

VALIDATION:
cd frontend && npm test
```

---

## Write integration tests for API endpoints

**Context**
Test auth and canvas CRUD with test database.

### Universal Agent Prompt
```
ROLE: Backend Integration Test Engineer

GOAL: Write integration tests for all API endpoints

CONTEXT: Test auth and canvas CRUD with test database.

FILES TO CREATE:
- backend/src/tests/integration/auth.integration.test.ts
- backend/src/tests/integration/canvas.integration.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Set up test database isolation
2. Test all auth endpoints (register, login)
3. Test all canvas endpoints (create, get, update, delete, list)
4. Ensure tests clean up after themselves
5. Use Supertest for HTTP assertions

VALIDATION:
cd backend && npm test -- integration
```

---

## Write integration tests for sync service

**Context**
Test conflict resolution scenarios.

### Universal Agent Prompt
```
ROLE: Backend Integration Test Engineer

GOAL: Write sync integration tests

CONTEXT: Test conflict resolution scenarios.

FILES TO CREATE:
- backend/src/tests/integration/sync.integration.test.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Write conflict detection tests
2. Write resolution flow tests
3. Write multiple client sync tests
4. Ensure edge case coverage

VALIDATION:
cd backend && npm test -- sync.integration.test.ts
```

---

## Set up E2E testing framework

**Context**
Configure Playwright or Cypress for end-to-end tests.

### Universal Agent Prompt
```
ROLE: E2E Test Engineer

GOAL: Set up Playwright or Cypress E2E framework

CONTEXT: Configure Playwright or Cypress for end-to-end tests.

FILES TO CREATE:
- tests/e2e/playwright.config.ts
- tests/e2e/cypress.config.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Choose Playwright or Cypress
2. Configure framework
3. Set up test server startup
4. Configure browser automation
5. Add screenshots on failure

VALIDATION:
cd tests/e2e && npm test
```

---

## Write E2E test for user registration and login

**Context**
Test complete auth flow from UI.

### Universal Agent Prompt
```
ROLE: E2E Test Engineer

GOAL: Write E2E test for registration and login flow

CONTEXT: Test complete auth flow from UI.

FILES TO CREATE:
- tests/e2e/auth.spec.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Navigate to register page
2. Fill form and submit
3. Verify redirect to dashboard
4. Test login flow
5. Test logout flow

VALIDATION:
cd tests/e2e && npm test -- auth.spec.ts
```

---

## Write E2E test for canvas creation and editing

**Context**
Test canvas operations from UI.

### Universal Agent Prompt
```
ROLE: E2E Test Engineer

GOAL: Write E2E test for canvas operations

CONTEXT: Test canvas operations from UI.

FILES TO CREATE:
- tests/e2e/canvas.spec.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create new canvas
2. Draw strokes on canvas
3. Verify auto-save indicator
4. Test pan and zoom
5. Verify data persistence

VALIDATION:
cd tests/e2e && npm test -- canvas.spec.ts
```

---

## Write E2E test for offline editing and sync

**Context**
Test offline workflow and sync on reconnect.

### Universal Agent Prompt
```
ROLE: E2E Test Engineer

GOAL: Write E2E test for offline workflow

CONTEXT: Test offline workflow and sync on reconnect.

FILES TO CREATE:
- tests/e2e/offline.spec.ts

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Go offline using browser API
2. Create and edit canvas
3. Verify local storage
4. Go online
5. Verify sync completes

VALIDATION:
cd tests/e2e && npm test -- offline.spec.ts
```

---

## Create login form component

**Context**
Email/password form with validation and submit handling.

### Universal Agent Prompt
```
ROLE: React UI Engineer

GOAL: Create login form component

CONTEXT: Email/password form with validation and submit handling.

FILES TO CREATE:
- frontend/src/components/auth/LoginForm.tsx

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create form with email and password inputs
2. Add client-side validation
3. Add error message display
4. Add submit button with loading state
5. Add link to registration page
6. Style with Tailwind CSS

VALIDATION:
cd frontend && npm run build
```

---

## Create registration form component

**Context**
Email/password/username form with validation.

### Universal Agent Prompt
```
ROLE: React UI Engineer

GOAL: Create registration form component

CONTEXT: Email/password/username form with validation.

FILES TO CREATE:
- frontend/src/components/auth/RegisterForm.tsx

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create form with email, password, username inputs
2. Add client-side validation
3. Add error message display
4. Add submit button with loading state
5. Add link to login page
6. Style with Tailwind CSS

VALIDATION:
cd frontend && npm run build
```

---

## Create canvas list view

**Context**
Display user's canvases with create and delete actions.

### Universal Agent Prompt
```
ROLE: React UI Engineer

GOAL: Create canvas list view component

CONTEXT: Display user's canvases with create and delete actions.

FILES TO CREATE:
- frontend/src/components/canvas/CanvasList.tsx

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Fetch and display list of canvas entries
2. Add create new canvas button
3. Add delete button per canvas
4. Open canvas on click
5. Add pagination for large lists
6. Style with Tailwind CSS

VALIDATION:
cd frontend && npm run build
```

---

## Create canvas editor layout

**Context**
Main editor UI with toolbar and canvas area.

### Universal Agent Prompt
```
ROLE: React UI Engineer

GOAL: Create canvas editor layout component

CONTEXT: Main editor UI with toolbar and canvas area.

FILES TO CREATE:
- frontend/src/components/canvas/CanvasEditor.tsx

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create layout with toolbar and canvas area
2. Canvas fills remaining space
3. Add responsive layout
4. Add loading states
5. Add error boundaries
6. Style with Tailwind CSS

VALIDATION:
cd frontend && npm run build
```

---

## Add toolbar controls

**Context**
Pan, zoom, grid toggle, save, logout controls.

### Universal Agent Prompt
```
ROLE: React UI Engineer

GOAL: Add toolbar controls to canvas editor

CONTEXT: Pan, zoom, grid toggle, save, logout controls.

FILES TO CREATE:
- frontend/src/components/canvas/Toolbar.tsx

FILES TO MODIFY:
- frontend/src/components/canvas/CanvasEditor.tsx

DETAILED STEPS:
1. Add pan tool button
2. Add zoom in/out buttons
3. Add grid toggle button
4. Add save button
5. Add back to list button
6. Add logout button
7. Style with Tailwind CSS

VALIDATION:
cd frontend && npm run build
```

---

## Add canvas title editing

**Context**
Inline title edit with auto-save.

### Universal Agent Prompt
```
ROLE: React UI Engineer

GOAL: Add inline title editing to canvas editor

CONTEXT: Inline title edit with auto-save.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- frontend/src/components/canvas/CanvasEditor.tsx

DETAILED STEPS:
1. Add click to edit title functionality
2. Add Enter to save
3. Add Escape to cancel
4. Add auto-save on blur
5. Add validation for empty title

VALIDATION:
cd frontend && npm run build
```

---

## Create environment configuration

**Context**
Document all required environment variables.

### Universal Agent Prompt
```
ROLE: DevOps Engineer

GOAL: Create environment configuration templates

CONTEXT: Document all required environment variables.

FILES TO CREATE:
- .env.production.example
- .env.development.example
- docs/ENVIRONMENT.md

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create production environment template
2. Create development environment template
3. Document each variable in ENVIRONMENT.md
4. Include secret generation guide

VALIDATION:
cat docs/ENVIRONMENT.md
```

---

## Add build scripts

**Context**
Create scripts for building frontend and backend.

### Universal Agent Prompt
```
ROLE: Build Engineer

GOAL: Add build scripts for frontend and backend

CONTEXT: Create scripts for building frontend and backend.

FILES TO CREATE:
_None_

FILES TO MODIFY:
- backend/package.json
- frontend/package.json
- package.json

DETAILED STEPS:
1. Add frontend build script
2. Add backend build script
3. Test production build
4. Document build artifacts in root package.json

VALIDATION:
npm run build
```

---

## Create Docker configuration

**Context**
Dockerfile and docker-compose for deployment.

### Universal Agent Prompt
```
ROLE: DevOps Engineer

GOAL: Create Docker configuration for deployment

CONTEXT: Dockerfile and docker-compose for deployment.

FILES TO CREATE:
- backend/Dockerfile
- frontend/Dockerfile
- docker-compose.yml
- .dockerignore

FILES TO MODIFY:
_None_

DETAILED STEPS:
1. Create Dockerfile for backend with Node.js
2. Create Dockerfile for frontend with multi-stage build
3. Create docker-compose.yml with PostgreSQL, backend, frontend services
4. Configure production-ready settings
5. Create .dockerignore files

VALIDATION:
docker-compose config
```

---

## Set up database migration scripts

**Context**
Production migration scripts with rollback.

### Universal Agent Prompt
```
ROLE: Database Engineer

GOAL: Set up production migration scripts

CONTEXT: Production migration scripts with rollback.

FILES TO CREATE:
- backend/scripts/migrate.sh
- backend/scripts/rollback.sh
- backend/scripts/backup.sh

FILES TO MODIFY:
- backend/package.json

DETAILED STEPS:
1. Create migration runner script
2. Create rollback script
3. Create database backup script
4. Document migration process
5. Add recommendations for backup

VALIDATION:
cd backend && npm run db:migrate -- --dry-run
```

---

## Create deployment documentation

**Context**
Step-by-step deployment guide.

### Universal Agent Prompt
```
ROLE: Technical Writer

GOAL: Create comprehensive deployment documentation

CONTEXT: Step-by-step deployment guide.

FILES TO CREATE:
- docs/DEPLOYMENT.md

FILES TO MODIFY:
- README.md

DETAILED STEPS:
1. Document prerequisites
2. Create step-by-step deployment guide
3. Add troubleshooting guide
4. Document rollback procedures
5. Update README with deployment link

VALIDATION:
cat docs/DEPLOYMENT.md
```
