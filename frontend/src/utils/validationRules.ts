// 재사용 가능한 validation rule 모음
// 각 rule은 (value: string) => true | '에러메시지' 형태

export const rules = {
  required: (msg = '필수 입력 항목입니다.') =>
    (v: string) => !!v.trim() || msg,

  email: () =>
    (v: string) => {
      if (!v.trim()) return '이메일을 입력해주세요.'
      return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v) || '올바른 이메일 형식이 아닙니다.'
    },

  minLength: (n: number, msg?: string) =>
    (v: string) => v.length >= n || (msg ?? `${n}자 이상 입력해주세요.`),

  maxLength: (n: number, msg?: string) =>
    (v: string) => v.length <= n || (msg ?? `${n}자 이하로 입력해주세요.`),

  password: () =>
    (v: string) => {
      if (!v) return '비밀번호를 입력해주세요.'
      if (v.length < 8) return '8자 이상 입력해주세요.'
      if (!/[a-zA-Z]/.test(v)) return '영문자를 포함해야 합니다.'
      if (!/[0-9]/.test(v)) return '숫자를 포함해야 합니다.'
      return true
    },

  phone: (required = false) =>
    (v: string) => {
      if (!v.trim()) return required ? '연락처를 입력해주세요.' : true
      return /^01[016789]-?\d{3,4}-?\d{4}$/.test(v.replace(/-/g, '').replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3'))
        || '올바른 휴대폰 번호 형식이 아닙니다. (예: 010-1234-5678)'
    },

  name: () =>
    (v: string) => {
      if (!v.trim()) return '이름을 입력해주세요.'
      if (v.trim().length < 2) return '2자 이상 입력해주세요.'
      return true
    },

  address: () =>
    (v: string) => {
      if (!v.trim()) return '배송 주소를 입력해주세요.'
      if (v.trim().length < 5) return '정확한 주소를 입력해주세요.'
      return true
    },
}
