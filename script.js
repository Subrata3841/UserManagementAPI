// Select DOM elements
const userForm = document.getElementById("userForm");
const userTableBody = document.getElementById("userTableBody");
const nameInput = document.getElementById("name");
const emailInput = document.getElementById("email");
const userIdInput = document.getElementById("userId");
const submitButton = document.getElementById("submitButton");
const deleteButton = document.getElementById("deleteButton");
const messageBox = document.getElementById("messageBox");

let users = [];
let selectedUserIndex = null;

/**
 * Renders all users to the HTML table
 */
function renderUsers() {
    userTableBody.innerHTML = "";

    users.forEach((user, index) => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td data-label="ID">${user.id}</td>
            <td data-label="Name">${user.name}</td>
            <td data-label="Email">${user.email}</td>
            <td data-label="Actions">
                <button class="edit-btn" title="Edit" onclick="editUser(${index})">✏️</button>
                <button class="delete-btn" title="Delete" onclick="deleteUser(${index})">🗑️</button>
            </td>
        `;

        userTableBody.appendChild(row);
    });
}

/**
 * Displays a message in the UI (success or error)
 */
function showMessage(message, type = "success") {
    messageBox.textContent = message;
    messageBox.style.display = "block";
    messageBox.style.backgroundColor = type === "error" ? "#fdecea" : "#eafaf1";
    messageBox.style.color = type === "error" ? "#e74c3c" : "#28a745";

    setTimeout(() => {
        messageBox.style.display = "none";
    }, 3000);
}

/**
 * Handles user form submission (add or update)
 */
userForm.addEventListener("submit", function (e) {
    e.preventDefault();

    const name = nameInput.value.trim();
    const email = emailInput.value.trim();

    if (!name || !email) {
        showMessage("Please enter both name and email.", "error");
        return;
    }

    const user = { name, email };

    if (selectedUserIndex === null) {
        user.id = Date.now(); // Unique ID
        users.push(user);
        showMessage("User added successfully.");
    } else {
        users[selectedUserIndex].name = name;
        users[selectedUserIndex].email = email;
        showMessage("User updated.");
        selectedUserIndex = null;
        submitButton.textContent = "Add User";
    }

    userForm.reset();
    renderUsers();
});

/**
 * Deletes selected user from the main delete button
 */
deleteButton.addEventListener("click", () => {
    if (selectedUserIndex !== null) {
        users.splice(selectedUserIndex, 1);
        showMessage("User deleted.");
        selectedUserIndex = null;
        userForm.reset();
        submitButton.textContent = "Add User";
        renderUsers();
    } else {
        showMessage("No user selected to delete.", "error");
    }
});

/**
 * Load selected user into form for editing
 */
window.editUser = function (index) {
    const user = users[index];
    nameInput.value = user.name;
    emailInput.value = user.email;
    userIdInput.value = user.id;
    selectedUserIndex = index;
    submitButton.textContent = "Update User";
};

/**
 * Delete user directly from the table row (with confirmation)
 */
window.deleteUser = function (index) {
    const confirmDelete = confirm("Are you sure you want to delete this user?");
    if (confirmDelete) {
        users.splice(index, 1);
        showMessage("User deleted.");
        selectedUserIndex = null;
        userForm.reset();
        submitButton.textContent = "Add User";
        renderUsers();
    }
};
