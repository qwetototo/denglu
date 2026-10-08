/**
 * 登录页面逻辑
 * <p>
 * 处理表单提交、调用后端接口、保存用户信息到 localStorage
 * 通过 Nginx 反向代理访问后端，请求地址走相对路径
 * </p>
 */

// ✅ 关键修改 1：改为空字符串，走相对路径
// 请求会变成 http://localhost:8066/api/auth/login，由 Nginx 转发到后端 8080
const API_BASE_URL = '';

// 获取页面元素
const loginForm = document.getElementById('loginForm');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const submitBtn = document.getElementById('submitBtn');
const messageDiv = document.getElementById('message');

/**
 * 显示消息提示
 *
 * @param {string} text 提示文字
 * @param {string} type 类型：success / error，默认空
 */
function showMessage(text, type = '') {
  messageDiv.textContent = text;
  messageDiv.className = 'message ' + type;
}

/**
 * 表单提交事件处理
 */
loginForm.addEventListener('submit', async (e) => {
  // 阻止表单默认提交（避免页面刷新）
  e.preventDefault();

  // 获取输入值并去除首尾空格
  const username = usernameInput.value.trim();
  const password = passwordInput.value;

  // 前端基础校验
  if (!username || !password) {
    showMessage('用户名和密码不能为空', 'error');
    return;
  }

  // 禁用按钮，防止重复提交
  submitBtn.disabled = true;
  submitBtn.textContent = '登录中...';
  showMessage('');

  try {
    // ✅ 关键修改 2：接口地址补上 "in"，正确为 /api/auth/login
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username, password })
    });

    // 解析 JSON 响应
    const result = await response.json();

    // 判断业务是否成功（统一响应体中的 code 为 200）
    if (result.code === 200 && result.data) {
      // 提取登录返回的数据
      const { token, user } = result.data;

      // ✅ 将用户信息保存到 localStorage，供后续页面使用
      localStorage.setItem('userInfo', JSON.stringify({
        token: token,
        id: user.id,
        username: user.username,
        name: user.name
      }));

      // 显示成功提示
      showMessage(`欢迎回来，${user.name}！正在跳转...`, 'success');

      // 延迟跳转到个人中心
      setTimeout(() => {
        window.location.href = 'profile.html';
      }, 800);
    } else {
      // 业务失败（用户名或密码错误等）
      showMessage(result.message || '登录失败', 'error');
    }
  } catch (error) {
    // 网络异常或解析异常
    console.error('登录请求异常:', error);
    showMessage('无法连接服务器，请确认后端已启动', 'error');
  } finally {
    // 恢复按钮状态
    submitBtn.disabled = false;
    submitBtn.textContent = '登录';
  }
});