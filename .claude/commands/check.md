# /project:check — 전체 상태 점검

다음을 순서대로 실행해서 결과를 보고해줘:

1. **프론트엔드 타입 체크**
   ```
   cd frontend && npx vue-tsc --noEmit
   ```
   에러가 없으면 ✅, 있으면 에러 목록과 파일명 표시

2. **백엔드 API 헬스 체크** — 아래 엔드포인트 curl로 확인
   - `http://localhost:8080/api/faq`
   - `http://localhost:8080/api/events`
   - `http://localhost:8080/api/coupons`
   각각 HTTP 상태코드와 data 배열 개수 출력

3. **프론트엔드 dev 서버** — `http://localhost:3000` 접근 가능한지 확인

4. **미커밋 변경사항** — `git status` 요약

모든 결과를 표 또는 체크리스트 형식으로 보여줘.
