/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        // 기존 primary 팔레트 유지 (red-500 #ef4444 호환) + 음영 추가
        primary: {
          50:  '#fef2f2',
          100: '#fee2e2',
          200: '#fecaca',
          400: '#f87171',
          500: '#ef4444',
          600: '#dc2626',
          700: '#b91c1c',
        },
        // 로켓배송 톤 (블루)
        rocket: {
          50:  '#eaf3ff',
          100: '#d4e7ff',
          500: '#0074e4',
          600: '#005bb5',
        },
        // 와우/특가 강조 (옐로우)
        deal: {
          100: '#fff5d6',
          400: '#ffba00',
          500: '#ff9500',
          700: '#b07700',
        },
      }
    }
  },
  plugins: []
}
