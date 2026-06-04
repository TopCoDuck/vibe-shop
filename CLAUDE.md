# Vibe Shop — Claude Code 가이드

## 프로젝트 개요

쿠팡 스타일 이커머스 플랫폼. Spring Boot 백엔드 + Vue 3 프론트엔드 모노레포.

## 기술 스택

| 영역 | 기술 |
|------|------|
| Backend | Spring Boot 3.5.0, JPA, Spring Security + JWT, MySQL |
| Frontend | Vue 3, TypeScript, Pinia, Vue Router, Tailwind CSS |
| JDK | Eclipse Temurin 25 (`C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot`) |
| DB | MySQL — `vibeshop` (user: `viber` / `viber`) |
| Dev 서버 | Backend `:8080`, Frontend `:3000` |

## 디렉토리 구조

```
vibe-shop/
├── backend/                         # Spring Boot
│   └── src/main/java/com/vibeshop/
│       ├── domain/                  # 도메인별 패키지 (auth, cart, coupon, event, faq, inquiry, order, product, user, wishlist)
│       │   └── {domain}/
│       │       ├── entity/
│       │       ├── repository/
│       │       ├── dto/
│       │       ├── service/
│       │       └── controller/
│       └── global/
│           ├── common/              # ApiResponse, BaseEntity
│           ├── config/              # SecurityConfig, CorsConfig
│           ├── exception/           # BusinessException, GlobalExceptionHandler
│           └── security/            # JwtTokenProvider, JwtAuthenticationFilter
├── frontend/                        # Vue 3
│   └── src/
│       ├── api/                     # axios 래퍼 (auth, cart, coupon, event, faq, inquiry, orders, products, wishlist)
│       ├── components/              # 재사용 컴포넌트 (FormField.vue, ProductCard.vue 등)
│       ├── layouts/                 # DefaultLayout.vue, AdminLayout.vue
│       ├── pages/                   # 라우트별 페이지
│       ├── stores/                  # Pinia 스토어 (auth, cart, wishlist, category)
│       ├── types/index.ts           # 공통 TypeScript 타입
│       └── utils/                   # axios.ts, validationRules.ts, productDisplay.ts
└── scripts/                         # DB 시드 SQL (coupon_seed.sql, event_seed.sql, faq_seed.sql)
```

## 핵심 규칙

### ❗ 커밋 규칙
**커밋은 사용자가 명시적으로 요청할 때만 수행한다.**

### 코드 컨벤션
- **Backend**: 도메인별 패키지 분리, `BaseEntity` 상속, `ApiResponse<T>` 응답 래퍼 사용
- **Frontend**: Composition API + `<script setup>`, 타입은 `src/types/index.ts`에 중앙 관리
- **Tailwind**: 커스텀 색상 `rocket`(파란색 #0074e4), `deal`(앰버색 #ffba00) 사용
- **FormField.vue**: 입력 컴포넌트는 재사용 FormField 활용 (dirty/blurred 유효성 검사)

### API 응답 형식
```json
{ "success": true, "message": "success", "data": { ... } }
```

## 주요 명령어

### Backend 빌드 & 실행
```powershell
# PowerShell에서 실행 (JAVA_HOME 필수)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
cd backend
.\gradlew.bat bootJar --no-daemon   # 빌드
# JAR 실행: java -jar build/libs/vibe-shop-backend-*.jar
```

### Frontend 개발 서버
```bash
cd frontend
npm run dev          # :3000 포트
npm run build        # 프로덕션 빌드
npx vue-tsc --noEmit # 타입 체크
```

### DB 작업
```bash
mysql -u viber -pviber --default-character-set=utf8mb4 vibeshop
# 시드 실행
mysql -u viber -pviber --default-character-set=utf8mb4 vibeshop < scripts/coupon_seed.sql
```

### API 테스트
```bash
curl http://localhost:8080/api/faq
curl http://localhost:8080/api/events
curl http://localhost:8080/api/coupons
```

## 인증 흐름

1. `POST /api/auth/login` → `accessToken`, `refreshToken` 반환
2. 이후 요청: `Authorization: Bearer {accessToken}` 헤더 포함
3. 프론트: `src/utils/axios.ts`가 토큰 자동 주입 + 401 시 갱신 처리

## 도메인 목록

| 도메인 | 엔드포인트 | 비고 |
|--------|-----------|------|
| auth | `/api/auth/**` | 공개 |
| products | `GET /api/products/**` | 공개 |
| categories | `GET /api/categories/**` | 공개 |
| faq | `GET /api/faq` | 공개, admin CRUD |
| events | `GET /api/events` | 공개 |
| coupons | `GET /api/coupons` | 공개; 다운로드/내쿠폰은 인증 필요 |
| cart, wishlist, orders, inquiries | `/api/**` | 로그인 필요 |
| admin | `/api/admin/**` | ROLE_ADMIN |

## GitHub

- 원격: `TopCoDuck/vibe-shop`
- 작업 브랜치: `feature/develop`
- PR 타깃: `main`
- GitHub 토큰: Windows Credential Manager (`git credential-manager get`)
