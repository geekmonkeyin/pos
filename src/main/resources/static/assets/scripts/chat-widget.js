// src/main/resources/static/chat-widget.js
document.addEventListener("DOMContentLoaded", function () {
  const chatIcon = document.createElement("div");
  chatIcon.id = "chatIcon";
  chatIcon.innerHTML = "💬";
  document.body.appendChild(chatIcon);

  const chatWindow = document.createElement("div");
  chatWindow.id = "chatWindow";
  chatWindow.innerHTML = `
    <div id="chatHeader">Chat with us</div>
    <div id="chatContent"></div>
    <input type="text" id="chatInput" placeholder="Type a message..." />
    <button id="chatSend">Send</button>
  `;


  document.body.appendChild(chatWindow);

const chatInput = document.getElementById("chatInput");

       chatInput.addEventListener('keydown', function(event) {
              if (event.key === 'Enter') {
                  chatSend();
              }
          });

  chatIcon.addEventListener("click", () => {
    chatWindow.style.display = chatWindow.style.display === "none" ? "block" : "none";
  });

  document.getElementById("chatSend").addEventListener("click", async () => {
        chatSend();
  });

  async function chatSend(){
      const input = document.getElementById("chatInput");
        const chatContent = document.getElementById("chatContent");
        //session id
        if (!window.sessionStorage.id) {
            window.sessionStorage.id = crypto.randomUUID();
        }
        const message = input.value;
        const sessionId = window.sessionStorage.id;
        if (!message) return;

        chatContent.innerHTML += `<div><b>You:</b> ${message}</div>`;
        input.value = "";

        const response = await fetch('/api/chat', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ message,sessionId })
        });
        const reply = await response.json(); // Parse JSON response
        chatContent.innerHTML += `<div><b>GPT:</b> ${reply.intentType}</div>`;
        chatContent.scrollTop = chatContent.scrollHeight;

  }

});
