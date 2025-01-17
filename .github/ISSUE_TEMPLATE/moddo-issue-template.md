---
name: moddo issue template
about: moddo's issue template
title: ''
labels: ''
assignees: ''

---

body:
  - type: textarea
    attributes:
      label:  💬 설명
      description: 새로운 기능에 대한 설명을 작성해 주세요.
      placeholder: 자세히 적을수록 좋습니다!
    validations:
      required: true
  - type: textarea
    attributes:
      label: 💫 작업 상세 내용
      description: 할 일을 체크박스 형태로 작성해주세요.
      placeholder: 최대한 세분화 해서 적어주세요!
    validations:
      required: true
