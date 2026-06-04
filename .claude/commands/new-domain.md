# /project:new-domain — 백엔드 도메인 스캐폴딩

$ARGUMENTS: 도메인 이름 (예: `review`, `point`, `notification`)

## 생성할 파일 구조

```
backend/src/main/java/com/vibeshop/domain/{name}/
├── entity/{Name}.java          # @Entity, BaseEntity 상속
├── repository/{Name}Repository.java
├── dto/{Name}Request.java
├── dto/{Name}Response.java
├── service/{Name}Service.java
└── controller/{Name}Controller.java
```

## 규칙

- 엔티티: `BaseEntity` 상속, Lombok (`@Getter`, `@Builder`, `@NoArgsConstructor`)
- 응답: `ApiResponse<T>` 래퍼 사용
- 서비스: `@Transactional(readOnly = true)` 기본
- 컨트롤러: `@RestController`, `@RequestMapping("/api/{name}s")`
- 프론트 API 파일: `frontend/src/api/{name}.ts`
- 타입: `frontend/src/types/index.ts`에 인터페이스 추가

도메인명을 받아서 보일러플레이트를 모두 생성하고, SecurityConfig에 필요한 권한도 추가해줘.
