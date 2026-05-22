// === CONFIGURACIÓN ===
const API_URL = "https://api-rest-ur95.onrender.com";;
let USUARIO = "";
let PASSWORD = "";

// === ELEMENTOS DEL DOM ===
const loginSection = document.getElementById("login-section");
const registerSection = document.getElementById("register-section");
const appSection = document.getElementById("app-section");

// Login
const loginUser = document.getElementById("login-user");
const loginPass = document.getElementById("login-pass");
const btnLogin = document.getElementById("btn-login");
const loginError = document.getElementById("login-error");
const showRegister = document.getElementById("show-register");
const showLogin = document.getElementById("show-login");

// Registro
const regUser = document.getElementById("reg-user");
const regEmail = document.getElementById("reg-email");
const regFullname = document.getElementById("reg-fullname");
const regPass = document.getElementById("reg-pass");
const btnRegister = document.getElementById("btn-register");
const registerError = document.getElementById("register-error");

// App
const userInfo = document.getElementById("user-info");
const btnLogout = document.getElementById("btn-logout");
const btnNewTask = document.getElementById("btn-new-task");
const taskList = document.getElementById("task-list");
const searchTitle = document.getElementById("search-title");
const filterCompleted = document.getElementById("filter-completed");
const filterPriority = document.getElementById("filter-priority");
const filterCategory = document.getElementById("filter-category");

// Dashboard
const dashTotal = document.getElementById("dash-total");
const dashPending = document.getElementById("dash-pending");
const dashCompleted = document.getElementById("dash-completed");
const dashOverdue = document.getElementById("dash-overdue");

// Modal
const taskModal = document.getElementById("task-modal");
const modalTitle = document.getElementById("modal-title");
const taskId = document.getElementById("task-id");
const taskTitleInput = document.getElementById("task-title");
const taskDesc = document.getElementById("task-desc");
const taskDeadline = document.getElementById("task-deadline");
const taskPriority = document.getElementById("task-priority");
const taskCategory = document.getElementById("task-category");
const btnSaveTask = document.getElementById("btn-save-task");
const btnCancelTask = document.getElementById("btn-cancel-task");

// === UTILIDADES ===
function headers() {
    return {
        "Authorization": "Basic " + btoa(USUARIO + ":" + PASSWORD),
        "Content-Type": "application/json"
    };
}

function showError(element, message) {
    element.textContent = message;
    element.classList.remove("hidden");
    setTimeout(() => element.classList.add("hidden"), 4000);
}

function formatDate(dateStr) {
    if (!dateStr) return "";
    const d = new Date(dateStr);
    return d.toLocaleDateString("es-ES", {
        day: "2-digit", month: "short", year: "numeric",
        hour: "2-digit", minute: "2-digit"
    });
}

function isOverdue(dateStr) {
    if (!dateStr) return false;
    return new Date(dateStr) < new Date();
}

// === NAVEGACIÓN ===
showRegister.addEventListener("click", (e) => {
    e.preventDefault();
    loginSection.classList.add("hidden");
    registerSection.classList.remove("hidden");
});

showLogin.addEventListener("click", (e) => {
    e.preventDefault();
    registerSection.classList.add("hidden");
    loginSection.classList.remove("hidden");
});

// === LOGIN ===
btnLogin.addEventListener("click", async () => {
    const user = loginUser.value.trim();
    const pass = loginPass.value.trim();

    if (!user || !pass) {
        showError(loginError, "Introduce usuario y contraseña");
        return;
    }

    USUARIO = user;
    PASSWORD = pass;

    try {
        const response = await fetch(API_URL + "/task", {
            method: "GET",
            headers: headers()
        });

        if (response.status === 401) {
            showError(loginError, "Usuario o contraseña incorrectos");
            return;
        }

        loginSection.classList.add("hidden");
        appSection.classList.remove("hidden");
        userInfo.textContent = "Hola, " + USUARIO;
        loadCategories();
        loadTasks();
        loadDashboard();
    } catch (error) {
        showError(loginError, "Error de conexión con el servidor");
    }
});

// Enter para login
loginPass.addEventListener("keydown", (e) => {
    if (e.key === "Enter") btnLogin.click();
});

// === REGISTRO ===
btnRegister.addEventListener("click", async () => {
    const username = regUser.value.trim();
    const email = regEmail.value.trim();
    const fullname = regFullname.value.trim();
    const password = regPass.value.trim();

    if (!username || !email || !fullname || !password) {
        showError(registerError, "Todos los campos son obligatorios");
        return;
    }

    try {
        const response = await fetch(API_URL + "/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email, fullname, password })
        });

        if (!response.ok) {
            showError(registerError, "Error al registrar. El usuario puede ya existir.");
            return;
        }

        registerSection.classList.add("hidden");
        loginSection.classList.remove("hidden");
        loginUser.value = username;
        loginPass.value = password;
    } catch (error) {
        showError(registerError, "Error de conexión con el servidor");
    }
});

// === LOGOUT ===
btnLogout.addEventListener("click", () => {
    USUARIO = "";
    PASSWORD = "";
    appSection.classList.add("hidden");
    loginSection.classList.remove("hidden");
    loginUser.value = "";
    loginPass.value = "";
});

// === CARGAR CATEGORÍAS ===
async function loadCategories() {
    try {
        const response = await fetch(API_URL + "/categories", {
            headers: headers()
        });
        const categories = await response.json();

        // Filtro
        filterCategory.innerHTML = '<option value="">Categoría</option>';
        categories.forEach(cat => {
            filterCategory.innerHTML += `<option value="${cat.id}">${cat.title}</option>`;
        });

        // Modal
        taskCategory.innerHTML = '<option value="">Sin categoría</option>';
        categories.forEach(cat => {
            taskCategory.innerHTML += `<option value="${cat.id}">${cat.title}</option>`;
        });
    } catch (error) {
        console.error("Error al cargar categorías:", error);
    }
}

// === CARGAR DASHBOARD ===
async function loadDashboard() {
    try {
        const response = await fetch(API_URL + "/task/dashboard", {
            headers: headers()
        });
        const data = await response.json();
        dashTotal.textContent = data.total;
        dashPending.textContent = data.pending;
        dashCompleted.textContent = data.completed;
        dashOverdue.textContent = data.overdue;
    } catch (error) {
        console.error("Error al cargar dashboard:", error);
    }
}

// === CARGAR TAREAS ===
async function loadTasks() {
    const title = searchTitle.value.trim();
    const completed = filterCompleted.value;
    const priority = filterPriority.value;
    const category = filterCategory.value;

    let url = API_URL + "/task";
    const params = [];

    if (title) params.push("title=" + encodeURIComponent(title));
    if (completed !== "") params.push("completed=" + completed);
    if (priority) params.push("priority=" + priority);
    if (category) params.push("category=" + category);

    if (params.length > 0) {
        url = API_URL + "/task/search?" + params.join("&");
    }

    try {
        const response = await fetch(url, { headers: headers() });
        const tasks = await response.json();
        renderTasks(tasks);
    } catch (error) {
        console.error("Error al cargar tareas:", error);
        taskList.innerHTML = '<div class="no-tasks">Error al cargar las tareas</div>';
    }
}

// === RENDERIZAR TAREAS ===
function renderTasks(tasks) {
    if (tasks.length === 0) {
        taskList.innerHTML = '<div class="no-tasks">No hay tareas. ¡Crea una nueva!</div>';
        return;
    }

    taskList.innerHTML = tasks.map(task => {
        const deadlineClass = (!task.completed && isOverdue(task.deadline)) ? "overdue" : "";
        const completedClass = task.completed ? "completed" : "";
        const checkedClass = task.completed ? "checked" : "";

        const priorityLabels = { LOW: "Baja", MEDIUM: "Media", HIGH: "Alta", URGENT: "Urgente" };

        return `
            <div class="task-card ${completedClass}" data-id="${task.id}">
                <div class="task-check ${checkedClass}" onclick="toggleComplete(${task.id}, ${!task.completed})"></div>
                <div class="task-body">
                    <div class="task-name">${task.title}</div>
                    ${task.description ? `<div class="task-desc">${task.description}</div>` : ""}
                    <div class="task-meta">
                        <span class="task-badge badge-priority-${task.priority}">${priorityLabels[task.priority] || task.priority}</span>
                        ${task.category ? `<span class="task-badge badge-category">${task.category}</span>` : ""}
                        ${task.deadline ? `<span class="task-deadline ${deadlineClass}">${formatDate(task.deadline)}</span>` : ""}
                    </div>
                </div>
                <div class="task-actions">
                    <button class="btn btn-small btn-outline" onclick="editTask(${task.id})">Editar</button>
                    <button class="btn btn-small btn-danger" onclick="deleteTask(${task.id})">Eliminar</button>
                </div>
            </div>
        `;
    }).join("");
}

// === TOGGLE COMPLETAR TAREA ===
async function toggleComplete(id, completed) {
    try {
        // Obtener tarea actual
        const getResp = await fetch(API_URL + "/task/" + id, { headers: headers() });
        const task = await getResp.json();

        const body = {
            title: task.title,
            description: task.description,
            deadline: task.deadline,
            completed: completed,
            priority: task.priority,
            categoryId: null
        };

        await fetch(API_URL + "/task/" + id, {
            method: "PUT",
            headers: headers(),
            body: JSON.stringify(body)
        });

        loadTasks();
        loadDashboard();
    } catch (error) {
        console.error("Error al actualizar tarea:", error);
    }
}

// === ELIMINAR TAREA ===
async function deleteTask(id) {
    if (!confirm("¿Seguro que quieres eliminar esta tarea?")) return;

    try {
        await fetch(API_URL + "/task/" + id, {
            method: "DELETE",
            headers: headers()
        });
        loadTasks();
        loadDashboard();
    } catch (error) {
        console.error("Error al eliminar tarea:", error);
    }
}

// === ABRIR MODAL PARA NUEVA TAREA ===
btnNewTask.addEventListener("click", () => {
    modalTitle.textContent = "Nueva tarea";
    taskId.value = "";
    taskTitleInput.value = "";
    taskDesc.value = "";
    taskDeadline.value = "";
    taskPriority.value = "MEDIUM";
    taskCategory.value = "";
    taskModal.classList.remove("hidden");
});

// === ABRIR MODAL PARA EDITAR TAREA ===
async function editTask(id) {
    try {
        const response = await fetch(API_URL + "/task/" + id, { headers: headers() });
        const task = await response.json();

        modalTitle.textContent = "Editar tarea";
        taskId.value = id;
        taskTitleInput.value = task.title || "";
        taskDesc.value = task.description || "";
        taskPriority.value = task.priority || "MEDIUM";

        if (task.deadline) {
            const d = new Date(task.deadline);
            const offset = d.getTimezoneOffset();
            d.setMinutes(d.getMinutes() - offset);
            taskDeadline.value = d.toISOString().slice(0, 16);
        } else {
            taskDeadline.value = "";
        }

        taskCategory.value = "";
        taskModal.classList.remove("hidden");
    } catch (error) {
        console.error("Error al cargar tarea:", error);
    }
}

// === GUARDAR TAREA (crear o editar) ===
btnSaveTask.addEventListener("click", async () => {
    const title = taskTitleInput.value.trim();
    if (!title) {
        alert("El título es obligatorio");
        return;
    }

    const body = {
        title: title,
        description: taskDesc.value.trim(),
        deadline: taskDeadline.value ? taskDeadline.value + ":00" : null,
        completed: false,
        priority: taskPriority.value,
        categoryId: taskCategory.value ? parseInt(taskCategory.value) : null
    };

    const id = taskId.value;
    const method = id ? "PUT" : "POST";
    const url = id ? API_URL + "/task/" + id : API_URL + "/task";

    try {
        const response = await fetch(url, {
            method: method,
            headers: headers(),
            body: JSON.stringify(body)
        });

        if (!response.ok) {
            alert("Error al guardar la tarea");
            return;
        }

        taskModal.classList.add("hidden");
        loadTasks();
        loadDashboard();
    } catch (error) {
        console.error("Error al guardar tarea:", error);
    }
});

// === CERRAR MODAL ===
btnCancelTask.addEventListener("click", () => {
    taskModal.classList.add("hidden");
});

taskModal.addEventListener("click", (e) => {
    if (e.target === taskModal) {
        taskModal.classList.add("hidden");
    }
});

// === FILTROS Y BÚSQUEDA ===
searchTitle.addEventListener("input", debounce(loadTasks, 400));
filterCompleted.addEventListener("change", loadTasks);
filterPriority.addEventListener("change", loadTasks);
filterCategory.addEventListener("change", loadTasks);

function debounce(fn, delay) {
    let timer;
    return function (...args) {
        clearTimeout(timer);
        timer = setTimeout(() => fn.apply(this, args), delay);
    };
}