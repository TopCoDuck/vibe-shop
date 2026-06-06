# /project:pr — Pull Request 생성

대상 레포: `TopCoDuck/vibe-shop` / 브랜치: `feature/develop` → `main`

## 실행 순서

1. **미커밋 변경 확인** — `git status`, `git diff --stat`
   - 변경사항이 있으면 스테이징 후 커밋 (커밋 메시지는 변경 내용 기반으로 작성)
   - 커밋 메시지 끝에 `Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>` 추가

2. **Push** — `git push origin feature/develop`

3. **기존 PR 확인** — GitHub API로 열린 PR 조회
   - 이미 있으면 기존 PR URL을 `<pr-created>` 태그로 출력
   - 없으면 새 PR 생성

4. **PR 생성** (없을 때만) — PowerShell로 GitHub API 호출
   - GitHub 토큰: `git credential-manager get` (host=github.com)
   - 제목: 이번 변경사항을 요약한 한 줄
   - 본문: 변경 내용, 기술적 세부사항, 리뷰어 참고사항 포함

5. **PR URL을 `<pr-created>` 태그로 출력**
