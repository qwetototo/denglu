<template>
  <div class="page">
    <div class="login-card">
      <h1>{{ isRegister ? '创建账号' : '欢迎回来' }}</h1>
      <p class="subtitle">{{ isRegister ? '注册后即可使用账号登录' : '请使用用户名和密码登录' }}</p>

      <div class="mode-switch" role="tablist">
        <button type="button" :class="{ active: !isRegister }" @click="switchMode(false)">登录</button>
        <button type="button" :class="{ active: isRegister }" @click="switchMode(true)">注册</button>
      </div>

      <form @submit.prevent="handleSubmit" novalidate>
        <div v-if="isRegister" class="form-group">
          <label for="name">姓名</label>
          <input id="name" type="text" v-model="name" class="input-field" :class="{ error: errors.name }" placeholder="请输入姓名" autocomplete="name" @input="clearError('name')" />
          <div class="error-message" :style="{ visibility: errors.name ? 'visible' : 'hidden' }">⚠️ {{ errors.name || '&nbsp;' }}</div>
        </div>
        <!-- 用户名 -->
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            id="username"
            type="text"
            v-model="username"
            class="input-field"
            :class="{ error: errors.username }"
            placeholder="请输入用户名"
            autocomplete="username"
            @input="clearError('username')"
          />
          <div class="error-message" :style="{ visibility: errors.username ? 'visible' : 'hidden' }">
            ⚠️ {{ errors.username || '&nbsp;' }}
          </div>
        </div>

        <!-- 密码 -->
        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            type="password"
            v-model="password"
            class="input-field"
            :class="{ error: errors.password }"
            placeholder="请输入密码"
            :autocomplete="isRegister ? 'new-password' : 'current-password'"
            @input="clearError('password')"
          />
          <div class="error-message" :style="{ visibility: errors.password ? 'visible' : 'hidden' }">
            ⚠️ {{ errors.password || '&nbsp;' }}
          </div>
        </div>

        <div v-if="isRegister" class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input id="confirmPassword" type="password" v-model="confirmPassword" class="input-field" :class="{ error: errors.confirmPassword }" placeholder="请再次输入密码" autocomplete="new-password" @input="clearError('confirmPassword')" />
          <div class="error-message" :style="{ visibility: errors.confirmPassword ? 'visible' : 'hidden' }">⚠️ {{ errors.confirmPassword || '&nbsp;' }}</div>
        </div>

        <button type="submit" class="login-button" :disabled="isLoading">
          <span v-if="isLoading" class="spinner"></span>
          {{ isLoading ? (isRegister ? '注册中...' : '登录中...') : (isRegister ? '注 册' : '登 录') }}
        </button>
      </form>

      <div v-if="message.text" class="message-bar" :class="message.type">
        <span>{{ message.type === 'success' ? '✅' : '❌' }}</span> {{ message.text }}
      </div>

      <div class="demo-hint">
        请求会发送到 <code>/api/auth/login</code>，由 Nginx 转发到后端容器
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const isRegister = ref(false)
const username = ref('')
const password = ref('')
const name = ref('')
const confirmPassword = ref('')
const isLoading = ref(false)

const errors = reactive({ username: '', password: '', name: '', confirmPassword: '' })
const message = reactive({ text: '', type: 'success' })

function clearError(field) {
  errors[field] = ''
  message.text = ''
}

function switchMode(register) {
  isRegister.value = register
  message.text = ''
  Object.keys(errors).forEach((key) => { errors[key] = '' })
}

function validateForm() {
  let ok = true
  errors.username = ''
  errors.password = ''
  errors.name = ''
  errors.confirmPassword = ''

  const u = username.value.trim()
  if (!u) {
    errors.username = '用户名不能为空'
    ok = false
  } else if (u.length < 3) {
    errors.username = '用户名至少为3个字符'
    ok = false
  }

  if (!password.value) {
    errors.password = '密码不能为空'
    ok = false
  } else if (password.value.length < 6) {
    errors.password = '密码至少为6位字符'
    ok = false
  }
  if (isRegister.value) {
    if (!name.value.trim()) { errors.name = '姓名不能为空'; ok = false }
    if (confirmPassword.value !== password.value) { errors.confirmPassword = '两次输入的密码不一致'; ok = false }
  }
  return ok
}

async function handleSubmit() {
  message.text = ''
  if (!validateForm()) return

  isLoading.value = true
  try {
    const res = await fetch(isRegister.value ? '/api/auth/register' : '/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: username.value.trim(),
        password: password.value,
        ...(isRegister.value ? { name: name.value.trim() } : {})
      })
    })

    let data = null
    const ct = res.headers.get('content-type')
    if (ct && ct.includes('application/json')) {
      data = await res.json()
    } else {
      data = { message: (await res.text()) || '响应异常' }
    }

    if (res.ok) {
      message.type = 'success'
      message.text = data.message || (isRegister.value ? '注册成功，请登录' : '登录成功！欢迎回来 👋')
      if (isRegister.value) {
        isRegister.value = false
        password.value = ''
        confirmPassword.value = ''
      }
      console.log('后端返回:', data)
      // 通常这里保存 token 并跳转，例如：
      // localStorage.setItem('token', data.token)
      // router.push('/dashboard')
    } else {
      message.type = 'error'
      message.text = data.message || data.error || `登录失败 (${res.status})`
    }
  } catch (err) {
    console.error('请求出错:', err)
    message.type = 'error'
    message.text = '无法连接到服务器，请检查后端容器是否正常运行。'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1.5rem;
  background: linear-gradient(145deg, #f6f8fd 0%, #e9eef7 100%);
  font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', sans-serif;
}
.login-card {
  background: #fff;
  max-width: 440px;
  width: 100%;
  padding: 2.5rem 2rem;
  border-radius: 2rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.5);
  transition: transform 0.2s ease;
}
.login-card:hover { transform: scale(1.01); }

h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 0.25rem;
  letter-spacing: -0.02em;
}
.subtitle {
  font-size: 0.95rem;
  color: #64748b;
  margin: 0 0 2rem;
}
.mode-switch {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.25rem;
  padding: 0.25rem;
  margin-bottom: 1.5rem;
  background: #f1f5f9;
  border-radius: 0.75rem;
}
.mode-switch button {
  border: 0;
  border-radius: 0.55rem;
  padding: 0.6rem;
  color: #64748b;
  background: transparent;
  cursor: pointer;
  font-size: 0.95rem;
}
.mode-switch button.active {
  color: #1d4ed8;
  background: #fff;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.12);
}

.form-group { margin-bottom: 1.5rem; }
label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #334155;
  margin-bottom: 0.5rem;
}

.input-field {
  width: 100%;
  padding: 0.9rem 1.2rem;
  font-size: 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 1rem;
  background: #f8fafc;
  color: #0f172a;
  transition: all 0.2s ease;
  box-sizing: border-box;
}
.input-field:focus {
  outline: none;
  border-color: #3b82f6;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.15);
}
.input-field.error {
  border-color: #ef4444;
  background: #fef2f2;
}
.input-field::placeholder { color: #94a3b8; }

.error-message {
  font-size: 0.8rem;
  color: #ef4444;
  margin-top: 0.375rem;
  min-height: 1.2rem;
}

.login-button {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  border: none;
  border-radius: 1rem;
  cursor: pointer;
  box-shadow: 0 10px 15px -3px rgba(59, 130, 246, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  transition: all 0.2s ease;
}
.login-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 100%);
  transform: translateY(-1px);
}
.login-button:disabled { opacity: 0.7; cursor: not-allowed; }

.message-bar {
  padding: 0.9rem 1.2rem;
  border-radius: 1rem;
  font-size: 0.95rem;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
  animation: fadeIn 0.3s ease;
}
.message-bar.success {
  background: #ecfdf5;
  color: #065f46;
  border: 1px solid #a7f3d0;
}
.message-bar.error {
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.demo-hint {
  margin-top: 1.8rem;
  text-align: center;
  font-size: 0.8rem;
  color: #94a3b8;
  border-top: 1px dashed #cbd5e1;
  padding-top: 1.2rem;
}
.demo-hint code {
  background: #f1f5f9;
  padding: 0.2rem 0.5rem;
  border-radius: 0.375rem;
  font-size: 0.75rem;
  color: #475569;
}

.spinner {
  width: 1rem;
  height: 1rem;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-4px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
