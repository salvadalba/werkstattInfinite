# Tasks Plan — Werkstatt Infinite

## 📌 Global Assumptions
- Solo developer working on the project
- PostgreSQL 14+ available
- Node.js 18+ runtime
- Modern browser with Canvas API and Web Crypto API support
- Development environment with npm/yarn
- Git for version control

## ⚠️ Risks
- Web Crypto API compatibility in older browsers
- IndexedDB quota limits on mobile devices
- Performance degradation with large canvas data
- Complex conflict resolution edge cases
- JWT secret exposure in localStorage
- Sync data loss during extended offline periods
- Base64 encoding overhead for encrypted payloads
- Browser memory limits with many canvas entries

## 🧩 Epics
## Authentication Foundation
**Goal:** Implement secure user registration and login with JWT authentication

### ✅ Set up project structure and dependencies (small)

Initialize Node.js backend with Express, PostgreSQL client, bcrypt, jsonwebtoken, and React frontend with Tailwind CSS

**Acceptance Criteria**
- Backend package.json with all dependencies installed
- React app created with Tailwind CSS configured
- Environment variables template created
- Git repository initialized with .gitignore

**Dependencies**
_None_
### ✅ Design and create PostgreSQL database schema (small)

Create users table with indexes, write migration scripts, establish connection pooling

**Acceptance Criteria**
- Users table with id, email, username, password_hash, created_at, updated_at
- Unique index on email column
- Migration script runs successfully
- Connection pool configured (min 20, max 50)

**Dependencies**
- Set up project structure and dependencies
### ✅ Implement password hashing utilities (small)

Create bcrypt wrapper with cost factor 12, minimum 8 character validation

**Acceptance Criteria**
- Hash function validates minimum 8 characters
- Bcrypt cost factor set to 12
- Compare function returns boolean
- Unit tests for hash and compare

**Dependencies**
- Design and create PostgreSQL database schema
### ✅ Implement JWT token generation and validation (small)

Create JWT service with 7-day expiration, secret from environment

**Acceptance Criteria**
- Token contains user_id and email in payload
- Tokens expire after 7 days
- Validation returns decoded payload or throws error
- Unit tests for generate and validate

**Dependencies**
- Set up project structure and dependencies
### ✅ Create POST /api/auth/register endpoint (medium)

Implement user registration with email validation, duplicate checking, password hashing

**Acceptance Criteria**
- Accepts {email, password, username}
- Returns 400 for invalid email format
- Returns 409 for existing email
- Returns user object and JWT token on success
- Integration tests covering all scenarios

**Dependencies**
- Implement password hashing utilities
- Implement JWT token generation and validation
### ✅ Create POST /api/auth/login endpoint (small)

Implement user login with credential verification, token generation

**Acceptance Criteria**
- Accepts {email, password}
- Returns 401 for invalid credentials
- Returns user object and JWT token on success
- Integration tests for valid and invalid login

**Dependencies**
- Create POST /api/auth/register endpoint
### ✅ Implement JWT authentication middleware (small)

Create Express middleware to validate Bearer tokens on protected routes

**Acceptance Criteria**
- Extracts token from Authorization header
- Returns 401 for missing or invalid tokens
- Adds user object to request object on success
- Unit tests for middleware

**Dependencies**
- Implement JWT token generation and validation

## Canvas Backend CRUD
**Goal:** Implement encrypted canvas storage with version tracking

### ✅ Create CanvasEntries database table (small)

Create table with id, user_id, title, encrypted_data, version, timestamps, add indexes

**Acceptance Criteria**
- Table created with all required columns
- Foreign key constraint on user_id
- Index on user_id for query optimization
- Migration script tested and reversible

**Dependencies**
_None_
### ✅ Implement base64 validation utility (small)

Create validator for encrypted data format

**Acceptance Criteria**
- Function returns boolean for valid base64
- Rejects malformed base64 strings
- Unit tests with valid and invalid inputs

**Dependencies**
_None_
### ✅ Create POST /api/canvas endpoint (medium)

Implement canvas creation with encrypted data storage and version initialization

**Acceptance Criteria**
- Accepts {title, encrypted_data, version, created_at}
- Returns 401 for unauthorized
- Returns 400 for invalid base64 data
- Returns canvas object with generated id on success
- Integration tests covering all scenarios

**Dependencies**
- Create CanvasEntries database table
- Implement base64 validation utility
- Implement JWT authentication middleware
### ✅ Create GET /api/canvas/:id endpoint (small)

Implement canvas retrieval with ownership verification

**Acceptance Criteria**
- Returns 401 for unauthorized
- Returns 404 for not found or wrong user
- Returns full canvas object on success
- Integration tests for ownership checks

**Dependencies**
- Create POST /api/canvas endpoint
### ✅ Create PUT /api/canvas/:id endpoint (medium)

Implement canvas update with version conflict detection

**Acceptance Criteria**
- Accepts {title, encrypted_data, version}
- Returns 401 for unauthorized
- Returns 404 for not found
- Returns 409 for version conflict
- Returns updated canvas on success
- Integration tests for version conflicts

**Dependencies**
- Create GET /api/canvas/:id endpoint
### ✅ Create DELETE /api/canvas/:id endpoint (small)

Implement canvas deletion with ownership verification

**Acceptance Criteria**
- Returns 401 for unauthorized
- Returns 404 for not found
- Returns {id, deleted: true} on success
- Integration tests for deletion

**Dependencies**
- Create PUT /api/canvas/:id endpoint
### ✅ Create GET /api/canvas list endpoint (small)

Implement paginated canvas listing for authenticated user

**Acceptance Criteria**
- Returns 401 for unauthorized
- Returns {canvases: [], total: n} on success
- Canvases contain id, title, created_at, updated_at
- Supports pagination query parameters
- Integration tests for pagination

**Dependencies**
- Create DELETE /api/canvas/:id endpoint

## Client-Side Encryption
**Goal:** Implement secure client-side encryption for all canvas data

### ✅ Implement key derivation from password (small)

Create PBKDF2-based key derivation with salt generation

**Acceptance Criteria**
- deriveKey(password, salt) returns cryptographic key
- Generates random salt if not provided
- Uses appropriate iteration count
- Unit tests with known vectors

**Dependencies**
_None_
### ✅ Implement AES-GCM encryption (medium)

Create encrypt function using Web Crypto API with IV and auth tag

**Acceptance Criteria**
- encrypt(data, key) returns base64 string
- Includes random IV in output
- Includes authentication tag
- Unit tests with known vectors

**Dependencies**
- Implement key derivation from password
### ✅ Implement AES-GCM decryption (medium)

Create decrypt function that extracts IV, validates auth tag, returns data

**Acceptance Criteria**
- decrypt(encryptedData, key) returns original data
- Throws error for invalid auth tag
- Throws error for malformed input
- Unit tests with known vectors

**Dependencies**
- Implement AES-GCM encryption
### ✅ Create encryption service module (small)

Package encryption utilities into reusable React context/service

**Acceptance Criteria**
- Exports encrypt, decrypt, deriveKey functions
- TypeScript types for all interfaces
- Error handling for crypto failures
- Documentation for usage

**Dependencies**
- Implement AES-GCM decryption

## Canvas Rendering Engine
**Goal:** Build 60fps infinite canvas with Bauhaus grid system

### ✅ Set up Canvas API context and event handlers (small)

Initialize HTML5 Canvas, handle resize events, set up basic render loop

**Acceptance Criteria**
- Canvas fills viewport
- Resize handler updates canvas dimensions
- requestAnimationFrame loop running
- Component cleanup on unmount

**Dependencies**
_None_
### ✅ Implement viewport pan functionality (small)

Add mouse/touch drag to pan canvas viewport

**Acceptance Criteria**
- Drag updates viewport offset
- Rendering accounts for pan offset
- Smooth panning at 60fps
- Works with mouse and touch

**Dependencies**
- Set up Canvas API context and event handlers
### ✅ Implement viewport zoom functionality (medium)

Add wheel/pinch to zoom centered on cursor or gesture

**Acceptance Criteria**
- Wheel zooms in/out at cursor position
- Pinch gesture works on touch devices
- Zoom is clamped to min/max bounds
- Smooth zooming at 60fps

**Dependencies**
- Implement viewport pan functionality
### ✅ Create Bauhaus snap-to-grid system (medium)

Implement visible grid with snap points for strokes

**Acceptance Criteria**
- Grid lines render at configurable intervals
- Strokes snap to grid intersections
- Toggle to enable/disable snapping
- Grid scales with zoom

**Dependencies**
- Implement viewport zoom functionality
### ✅ Implement stroke drawing system (medium)

Capture mouse/touch strokes, render as canvas paths

**Acceptance Criteria**
- Mouse down starts stroke
- Move adds points to stroke
- Up finalizes stroke
- Strokes render at 60fps
- Stroke data structure defined

**Dependencies**
- Create Bauhaus snap-to-grid system
### ✅ Implement stroke batching optimization (medium)

Group strokes within same frame for efficient rendering

**Acceptance Criteria**
- Multiple strokes in same frame batched
- Rendering maintains 60fps with 100+ strokes
- Clear function to redraw all strokes

**Dependencies**
- Implement stroke drawing system
### ✅ Add stroke viewport virtualization (medium)

Only render strokes within or intersecting visible viewport

**Acceptance Criteria**
- Viewport bounds calculated each frame
- Strokes outside bounds skipped
- Performance test with 1000+ strokes
- No visible flicker on pan/zoom

**Dependencies**
- Implement stroke batching optimization

## Local-First State Management
**Goal:** Implement offline-capable state with local persistence

### ✅ Design and create global state structure (small)

Define state shape for user, canvases, current canvas, sync status

**Acceptance Criteria**
- TypeScript interface for State type
- Initial state values defined
- State update actions typed

**Dependencies**
_None_
### ✅ Implement React Context for state management (small)

Create StateContext and Provider with getState, setState, subscribe

**Acceptance Criteria**
- Context provides state and dispatcher
- setState performs shallow merge
- subscribe registers listeners
- Unit tests for state updates

**Dependencies**
- Design and create global state structure
### ✅ Implement localStorage persistence for auth (small)

Save and load JWT tokens from browser localStorage

**Acceptance Criteria**
- Token saved on login
- Token loaded on app init
- Token cleared on logout
- Handles corrupted data gracefully

**Dependencies**
- Implement React Context for state management
### ✅ Implement IndexedDB for canvas data (medium)

Create IndexedDB wrapper for storing encrypted canvas entries

**Acceptance Criteria**
- Database opened on app init
- Canvas entries saved to object store
- Canvas entries retrieved by id
- All canvases listed for user
- Handles quota exceeded errors

**Dependencies**
- Implement localStorage persistence for auth
### ✅ Create offline action queue (medium)

Queue API actions when offline, replay when online

**Acceptance Criteria**
- Actions queued when network unavailable
- Queue persisted to localStorage
- Actions replayed on reconnect
- Failed actions remain in queue
- Queue cleared on successful replay

**Dependencies**
- Implement IndexedDB for canvas data
### ✅ Implement online/offline detection (small)

Listen to online/offline events, update app state

**Acceptance Criteria**
- State updated on online event
- State updated on offline event
- Visual indicator shown to user
- Triggers queue replay/retry

**Dependencies**
- Create offline action queue

## Sync & Conflict Resolution
**Goal:** Implement bidirectional sync with conflict handling

### ✅ Implement change tracking for local edits (small)

Track all canvas modifications with timestamps and action types

**Acceptance Criteria**
- Each change tagged with timestamp
- Action type: create, update, delete
- Changes stored with canvas id
- Changes cleared after successful sync

**Dependencies**
_None_
### ✅ Create POST /api/sync endpoint (medium)

Backend sync endpoint with last_sync parameter and change delta processing

**Acceptance Criteria**
- Accepts {last_sync, local_changes}
- Returns {server_changes, sync_time}
- Returns 400 for invalid format
- Returns 401 for unauthorized
- Integration tests for sync scenarios

**Dependencies**
- Create GET /api/canvas list endpoint
### ✅ Implement client sync initiator (medium)

Client function to collect local changes and call sync API

**Acceptance Criteria**
- Collects changes since last_sync
- Formats sync request
- Processes server response
- Updates last_sync timestamp
- Handles network errors with retry

**Dependencies**
- Implement change tracking for local edits
- Create POST /api/sync endpoint
### ✅ Implement version conflict detection (small)

Detect when server version differs from local version during sync

**Acceptance Criteria**
- Compares local vs server versions
- Flags conflicts for resolution
- Returns conflict metadata
- Unit tests for conflict scenarios

**Dependencies**
- Implement client sync initiator
### ✅ Create conflict resolution UI (medium)

Prompt user to resolve conflicts with local/server/merge options

**Acceptance Criteria**
- Modal shows conflict details
- User can choose local version
- User can choose server version
- Resolution sent to API
- State updated after resolution

**Dependencies**
- Implement version conflict detection
### ✅ Implement automatic sync on interval (small)

Periodically trigger sync when online, with debounce on edits

**Acceptance Criteria**
- Sync triggers every 30 seconds when online
- Edits debounce sync for 5 seconds
- Sync skipped when offline
- Manual sync button available

**Dependencies**
- Create conflict resolution UI

## Auto-Save System
**Goal:** Implement debounced auto-save during canvas editing

### ✅ Create auto-save debouncer (small)

Implement 300ms debounce function for canvas save operations

**Acceptance Criteria**
- Debounce function delays execution
- Subsequent calls reset timer
- Final execution after 300ms of inactivity
- Cancel function available

**Dependencies**
_None_
### ✅ Integrate auto-save with stroke changes (small)

Trigger auto-save on stroke completion (mouse up)

**Acceptance Criteria**
- Each stroke end triggers debounced save
- Multiple rapid strokes result in single save
- Save includes all stroke data
- Success/failure indicated to user

**Dependencies**
- Create auto-save debouncer
- Implement stroke drawing system
### ✅ Add save status indicator (small)

Visual indicator for unsaved changes, saving, and saved states

**Acceptance Criteria**
- Shows 'unsaved' when edits pending
- Shows 'saving' during save operation
- Shows 'saved' after success
- Shows 'error' on save failure

**Dependencies**
- Integrate auto-save with stroke changes

## Security & Validation
**Goal:** Implement input validation, rate limiting, and security measures

### ✅ Add email format validation (small)

Server-side email validation using regex

**Acceptance Criteria**
- Validates email format on registration
- Returns 400 for invalid format
- Unit tests for valid/invalid emails

**Dependencies**
_None_
### ✅ Implement rate limiting middleware (small)

100 requests per minute per IP using express-rate-limit

**Acceptance Criteria**
- Rate limit applied to all API routes
- Returns 429 when limit exceeded
- Reset time included in response
- Configuration via environment variable

**Dependencies**
_None_
### ✅ Configure CORS restrictions (small)

Restrict CORS to origin domain

**Acceptance Criteria**
- CORS configured in Express
- Origin from environment variable
- Credentials allowed for cookies
- Preflight handled correctly

**Dependencies**
_None_
### ✅ Add SQL injection prevention (small)

Ensure all database queries use parameterized queries

**Acceptance Criteria**
- All queries use pg parameterization
- No string concatenation in queries
- Code review confirms safety
- Security tests for injection attempts

**Dependencies**
_None_
### ✅ Implement input sanitization (small)

Sanitize all user-generated content

**Acceptance Criteria**
- Title fields sanitized
- Username fields sanitized
- Length limits enforced
- Dangerous characters escaped

**Dependencies**
_None_
### ✅ Add httpOnly cookie for JWT (small)

Store JWT in httpOnly cookie in addition to localStorage

**Acceptance Criteria**
- Cookie set on login/register
- Cookie httpOnly flag enabled
- Cookie secure flag in production
- Cookie cleared on logout

**Dependencies**
- Create POST /api/auth/login endpoint

## Error Handling
**Goal:** Implement comprehensive error handling at all layers

### ✅ Create global error handler for Express (small)

Catch-all error handler with consistent JSON responses

**Acceptance Criteria**
- All errors caught by middleware
- Response format {error: string, code: string}
- Appropriate status codes
- Errors logged with context

**Dependencies**
_None_
### ✅ Implement React error boundary (small)

Error boundary component wrapping app, shows fallback UI

**Acceptance Criteria**
- Catches component errors
- Shows fallback UI with error details
- Errors logged to console
- Report button for remote logging

**Dependencies**
_None_
### ✅ Add exponential backoff for API retries (small)

Client-side retry logic with exponential backoff for failed sync

**Acceptance Criteria**
- Failed API calls trigger retry
- Delay doubles each retry (1s, 2s, 4s, 8s)
- Max 5 retries before giving up
- Backoff applied to sync failures

**Dependencies**
- Create global error handler for Express
### ✅ Create error reporting service (medium)

Remote error reporting for client-side errors

**Acceptance Criteria**
- Client errors sent to reporting endpoint
- Includes error stack, user agent, timestamp
- Opt-in/opt-out for privacy
- Error dashboard for monitoring

**Dependencies**
- Implement React error boundary

## Observability
**Goal:** Implement logging, tracing, and metrics

### ✅ Set up structured JSON logging (small)

Backend logging with error, warn, info, debug levels

**Acceptance Criteria**
- Log function with level parameter
- JSON output with timestamp, level, message
- Configurable log level via environment
- Logs written to stdout

**Dependencies**
_None_
### ✅ Add request ID injection (small)

Generate unique request ID for each API call, log through stack

**Acceptance Criteria**
- Request ID generated on ingress
- ID included in all log statements
- ID returned in response header
- Traces sync operations end-to-end

**Dependencies**
- Set up structured JSON logging
### ✅ Implement API response time metrics (medium)

Track p50, p95, p99 response times per endpoint

**Acceptance Criteria**
- Response time recorded for each request
- Metrics aggregated by endpoint
- Percentiles calculated
- Metrics endpoint returns JSON

**Dependencies**
- Add request ID injection
### ✅ Add canvas render FPS monitoring (small)

Track actual FPS during canvas rendering

**Acceptance Criteria**
- FPS calculated each second
- Metrics logged to console in dev
- Performance warnings if below 50fps
- Optional metrics reporting

**Dependencies**
- Add stroke viewport virtualization
### ✅ Track sync success/failure rates (small)

Monitor sync operations and outcomes

**Acceptance Criteria**
- Success counter incremented on good sync
- Failure counter incremented on bad sync
- Rate calculated and exposed
- Logged with context

**Dependencies**
- Implement automatic sync on interval
### ✅ Create metrics dashboard endpoint (small)

Admin endpoint returning aggregated metrics

**Acceptance Criteria**
- GET /api/metrics returns JSON
- Includes active user count
- Includes canvas entry counts
- Includes error rates by endpoint
- Requires admin authentication

**Dependencies**
- Implement API response time metrics
- Track sync success/failure rates

## Performance Optimization
**Goal:** Optimize rendering, API responses, and database performance

### ✅ Add Gzip compression for API responses (small)

Enable Gzip compression middleware in Express

**Acceptance Criteria**
- Compression middleware configured
- Applies to all API responses
- Encrypted payloads compressed
- Compression threshold appropriate

**Dependencies**
_None_
### ✅ Optimize database queries with indexes (small)

Review and add indexes for common query patterns

**Acceptance Criteria**
- EXPLAIN ANALYZE shows index usage
- Query times under 50ms for common operations
- Indexes added for foreign keys
- Index maintenance documented

**Dependencies**
- Create CanvasEntries database table
### ✅ Implement requestAnimationFrame scheduling (small)

Ensure canvas rendering uses rAF for 60fps target

**Acceptance Criteria**
- All render calls in rAF callback
- Multiple renders coalesced in single frame
- Frame time logged for monitoring
- Graceful degradation if slow

**Dependencies**
- Set up Canvas API context and event handlers
### ✅ Add connection pooling configuration (small)

Configure PostgreSQL pool sizes and timeouts

**Acceptance Criteria**
- Min pool size 20, max 50
- Connection timeout configured
- Idle timeout configured
- Pool metrics logged

**Dependencies**
- Design and create PostgreSQL database schema

## Testing Infrastructure
**Goal:** Set up comprehensive testing for unit, integration, and E2E

### ✅ Set up testing framework (small)

Configure Jest for unit tests, Supertest for API tests

**Acceptance Criteria**
- Jest configured with TypeScript
- Test environment setup scripts
- Coverage reporting enabled
- Supertest configured for API tests

**Dependencies**
_None_
### ✅ Write unit tests for auth module (small)

Test password hashing, token generation, token validation

**Acceptance Criteria**
- Password hash tests with various inputs
- Token generation validates payload
- Token validation rejects bad tokens
- Coverage above 90%

**Dependencies**
- Implement password hashing utilities
- Implement JWT token generation and validation
### ✅ Write unit tests for encryption module (small)

Test encrypt/decrypt with known test vectors

**Acceptance Criteria**
- Encrypt produces expected output
- Decrypt reverses encrypt
- Key derivation produces consistent keys
- Invalid inputs throw errors

**Dependencies**
- Implement key derivation from password
- Implement AES-GCM encryption
### ✅ Write unit tests for canvas renderer (small)

Test stroke rendering, viewport calculations, virtualization

**Acceptance Criteria**
- Viewport offset calculations correct
- Zoom calculations correct
- Virtualization bounds correct
- Stroke data structure tests

**Dependencies**
- Implement viewport pan functionality
- Implement viewport zoom functionality
### ✅ Write integration tests for API endpoints (medium)

Test auth and canvas CRUD with test database

**Acceptance Criteria**
- All auth endpoints tested
- All canvas endpoints tested
- Test database isolated
- Tests clean up after themselves

**Dependencies**
- Write unit tests for auth module
### ✅ Write integration tests for sync service (medium)

Test conflict resolution scenarios

**Acceptance Criteria**
- Conflict detection tests
- Resolution flow tests
- Multiple client sync tests
- Edge case coverage

**Dependencies**
- Create conflict resolution UI
### ✅ Set up E2E testing framework (small)

Configure Playwright or Cypress for end-to-end tests

**Acceptance Criteria**
- Framework configured
- Test server startup
- Browser automation working
- Screenshots on failure

**Dependencies**
_None_
### ✅ Write E2E test for user registration and login (medium)

Test complete auth flow from UI

**Acceptance Criteria**
- Navigate to register page
- Fill form and submit
- Verify redirect to dashboard
- Test login flow
- Test logout flow

**Dependencies**
- Set up E2E testing framework
- Create POST /api/auth/login endpoint
### ✅ Write E2E test for canvas creation and editing (medium)

Test canvas operations from UI

**Acceptance Criteria**
- Create new canvas
- Draw strokes on canvas
- Verify auto-save
- Test pan and zoom
- Verify data persistence

**Dependencies**
- Write E2E test for user registration and login
- Implement stroke drawing system
### ✅ Write E2E test for offline editing and sync (medium)

Test offline workflow and sync on reconnect

**Acceptance Criteria**
- Go offline
- Create and edit canvas
- Verify local storage
- Go online
- Verify sync completes

**Dependencies**
- Write E2E test for canvas creation and editing
- Implement automatic sync on interval

## Frontend UI Components
**Goal:** Build user interface for authentication and canvas management

### ✅ Create login form component (small)

Email/password form with validation and submit handling

**Acceptance Criteria**
- Email and password inputs
- Client-side validation
- Error message display
- Submit button with loading state
- Links to registration

**Dependencies**
- Create POST /api/auth/login endpoint
### ✅ Create registration form component (small)

Email/password/username form with validation

**Acceptance Criteria**
- Email, password, username inputs
- Client-side validation
- Error message display
- Submit button with loading state
- Links to login

**Dependencies**
- Create login form component
### ✅ Create canvas list view (medium)

Display user's canvases with create and delete actions

**Acceptance Criteria**
- List of canvas entries
- Create new canvas button
- Delete button per canvas
- Open canvas on click
- Pagination for large lists

**Dependencies**
- Create GET /api/canvas list endpoint
### ✅ Create canvas editor layout (small)

Main editor UI with toolbar and canvas area

**Acceptance Criteria**
- Toolbar with tools
- Canvas fill remaining space
- Responsive layout
- Loading states
- Error boundaries

**Dependencies**
_None_
### ✅ Add toolbar controls (small)

Pan, zoom, grid toggle, save, logout controls

**Acceptance Criteria**
- Pan tool button
- Zoom in/out buttons
- Grid toggle
- Save button
- Back to list button
- Logout button

**Dependencies**
- Create canvas editor layout
### ✅ Add canvas title editing (small)

Inline title edit with auto-save

**Acceptance Criteria**
- Click to edit title
- Enter to save
- Escape to cancel
- Auto-save on blur
- Validation for empty title

**Dependencies**
- Create canvas editor layout

## Deployment
**Goal:** Prepare application for production deployment

### ✅ Create environment configuration (small)

Document all required environment variables

**Acceptance Criteria**
- Production env template
- Development env template
- Documentation for each variable
- Secret generation guide

**Dependencies**
_None_
### ✅ Add build scripts (small)

Create scripts for building frontend and backend

**Acceptance Criteria**
- Frontend build script
- Backend build script
- Production build tested
- Build artifacts documented

**Dependencies**
_None_
### ✅ Create Docker configuration (medium)

Dockerfile and docker-compose for deployment

**Acceptance Criteria**
- Dockerfile for backend
- Dockerfile for frontend
- docker-compose with PostgreSQL
- Production-ready configuration

**Dependencies**
- Create environment configuration
- Add build scripts
### ✅ Set up database migration scripts (small)

Production migration scripts with rollback

**Acceptance Criteria**
- Migration script tested
- Rollback script tested
- Documentation for running migrations
- Backup recommendation

**Dependencies**
- Create Docker configuration
### ✅ Create deployment documentation (small)

Step-by-step deployment guide

**Acceptance Criteria**
- Prerequisites documented
- Step-by-step deployment
- Troubleshooting guide
- Rollback procedures

**Dependencies**
- Set up database migration scripts

## ❓ Open Questions
- What is the maximum encrypted payload size for canvas entries?
- Should we implement soft deletes for canvas entries?
- What is the retention policy for deleted user data?
- Should we support canvas sharing between users?
- What is the maximum stroke count per canvas before performance degrades?
- Should we implement canvas version history/rollback?
- What is the sync behavior for conflicting edits on the same canvas?
