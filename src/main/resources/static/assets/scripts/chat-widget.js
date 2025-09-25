// src/main/resources/static/chat-bot.js
document.addEventListener("DOMContentLoaded", () => {
  /* ====== Geekmonkey Theme ====== */
  const style = document.createElement("style");
  style.textContent = `
    :root{
      --bg:#F9FAFB; --card:#fff; --stroke:#E5E7EB; --text:#111827; --muted:#6B7280;
      --accent:#FF7A00; --accent-ink:#9A4D00; --radius:14px;
    }
    #gm-chat-fab{
      position:fixed; right:18px; bottom:18px; z-index:9999;
      width:56px; height:56px; border-radius:16px; display:grid; place-items:center;
      background:var(--accent); color:#fff; font-size:24px; cursor:pointer;
      box-shadow:0 14px 36px rgba(0,0,0,.18);
    }
    #gm-chat-fab:hover{ filter:brightness(1.05); }

    #gm-chat{
      position:fixed; right:18px; bottom:84px; z-index:10000; display:none;
      width:min(380px,92vw); height:580px; border-radius:var(--radius);
      background:var(--card); border:1px solid var(--stroke); overflow:hidden;
      box-shadow:0 22px 56px rgba(0,0,0,.22);
      display:grid; grid-template-rows:56px 1fr auto auto;
    }
    .gm-head{
      display:flex; align-items:center; justify-content:space-between; gap:10px;
      padding:0 14px; border-bottom:1px solid var(--stroke); background:#fff;
    }
    .gm-head .left{display:flex; align-items:center; gap:10px}
    .gm-logo{width:28px; height:28px; border-radius:8px; background:var(--accent); color:#fff; font-weight:700; display:grid; place-items:center}
    .gm-title{font-weight:700}
    .gm-head .btn{
      height:30px; padding:0 10px; border-radius:10px; border:1px solid var(--stroke); background:#fff; cursor:pointer;
    }

    .gm-body{ background:var(--bg); overflow:auto; padding:12px; }
    .msg{ display:grid; gap:4px; margin:8px 0; max-width:88%; }
    .msg.me{ justify-self:end; }
    .bubble{
      padding:10px 12px; border:1px solid var(--stroke); border-radius:12px; background:#fff; color:var(--text);
      box-shadow:0 4px 14px rgba(0,0,0,.04);
    }
    .msg.me .bubble{ background:#fff7f2; border-color:#ffd9bf; color:#7c3d00; }
    .meta{ font-size:11px; color:var(--muted) }

    .gm-suggest{
      background:#fff; border-top:1px solid var(--stroke);
      padding:10px; display:flex; gap:8px; flex-wrap:wrap;
    }
    .chip{
      border:none; background:var(--accent); color:#fff; border-radius:999px; height:32px; padding:0 12px; cursor:pointer; font-weight:600;
      box-shadow:0 8px 18px rgba(255,122,0,.25);
    }
    .chip.secondary{ background:#fff; color:var(--text); border:1px solid var(--stroke); box-shadow:none; }
    .chip[disabled]{ opacity:.6; cursor:not-allowed; }

    .gm-input{
      background:#fff; border-top:1px solid var(--stroke); padding:10px; display:flex; gap:8px; align-items:center;
    }
    .gm-input input{
      flex:1; height:38px; border:1px solid var(--stroke); border-radius:10px; padding:0 12px; background:#fff;
      font:14px system-ui,-apple-system,Segoe UI,Roboto,Inter,Arial,sans-serif; color:var(--text);
    }
    .gm-input .send{
      height:38px; padding:0 14px; border:none; border-radius:10px; background:var(--accent); color:#fff; font-weight:700; cursor:pointer;
    }

    /* skeleton chips */
    .skeleton{
      height:32px; width:110px; border-radius:999px; background:linear-gradient(90deg,#f3f4f6,#e5e7eb,#f3f4f6);
      background-size:200% 100%; animation:gmPulse 1.15s ease-in-out infinite;
    }
    @keyframes gmPulse{ 0%{background-position:0 0} 100%{background-position:-200% 0} }

    /* typing dots */
    .typing{
      display:inline-flex; gap:3px; align-items:center;
    }
    .dot{ width:6px; height:6px; border-radius:50%; background:#9CA3AF; animation:blink 1.2s infinite; }
    .dot:nth-child(2){ animation-delay:.2s }
    .dot:nth-child(3){ animation-delay:.4s }
    @keyframes blink{ 0%,80%,100%{opacity:.2} 40%{opacity:1} }
  `;
  document.head.appendChild(style);

  /* ====== DOM ====== */
  const fab = document.createElement("div");
  fab.id = "gm-chat-fab";
  fab.title = "Chat";
  fab.textContent = "💬";
  document.body.appendChild(fab);

  const wrap = document.createElement("div");
  wrap.id = "gm-chat";
  wrap.innerHTML = `
    <div class="gm-head">
      <div class="left">
        <div class="gm-logo">GM</div>
        <div class="gm-title">Geekmonkey Assistant</div>
      </div>
      <div class="right">
        <button class="btn" id="gm-restart">Start over</button>
        <button class="btn" id="gm-close">Close</button>
      </div>
    </div>
    <div class="gm-body" id="gm-body" aria-live="polite"></div>
    <div class="gm-suggest" id="gm-suggest">
      <div class="skeleton"></div>
      <div class="skeleton" style="width:140px"></div>
      <div class="skeleton" style="width:90px"></div>
    </div>
    <div class="gm-input">
      <input id="gm-input" type="text" placeholder="Type your question..." />
      <button class="send" id="gm-send">Send</button>
    </div>
  `;
  document.body.appendChild(wrap);

  const bodyEl = document.getElementById("gm-body");
  const suggestEl = document.getElementById("gm-suggest");
  const inputEl = document.getElementById("gm-input");

  /* ====== Session ====== */
  function ensureSession(reset=false){
    try{
      if (reset) delete sessionStorage.gmChatSessionId;
      if (!sessionStorage.gmChatSessionId){
        sessionStorage.gmChatSessionId = (crypto?.randomUUID?.() || (Date.now() + "_" + Math.random()));
      }
      return sessionStorage.gmChatSessionId;
    }catch{
      return Date.now() + "_" + Math.random();
    }
  }
  function getSessionId(){ return ensureSession(false); }

  /* ====== Open / Close ====== */
  fab.addEventListener("click", () => {
    const nowOpen = wrap.style.display !== "grid";
    wrap.style.display = nowOpen ? "grid" : "none";
    if (nowOpen && !suggestEl.dataset.ready) boot();
  });
  document.getElementById("gm-close").addEventListener("click", () => wrap.style.display = "none");
  document.getElementById("gm-restart").addEventListener("click", restart);

  /* ====== Boot ====== */
  async function boot(){
    addBot("Hi! You can type your question or pick one of the suggested topics below.");
    await loadSuggestions();
  }

  /* ====== Suggestions (preselected questions) ====== */
  async function loadSuggestions(){
    setSuggestLoading(true);
    try{
      const res = await fetch(`/api/chat/options?sessionId=${encodeURIComponent(getSessionId())}`);
      const opts = await res.json(); // [{id,label}]
      renderSuggestions(Array.isArray(opts.options) ? opts.options : []);
      suggestEl.dataset.ready = "1";
    }catch(e){
      renderSuggestions([]);
      addBot("Couldn’t load suggestions. Please try again or type your question.");
    }finally{
      setSuggestLoading(false);
      scrollBottom();
    }
  }

  function renderSuggestions(options){
    suggestEl.innerHTML = "";
    if (!options.length){
      const btn = document.createElement("button");
      btn.className = "chip secondary";
      btn.textContent = "Reload suggestions";
      btn.addEventListener("click", loadSuggestions);
      suggestEl.appendChild(btn);
      return;
    }
    options.forEach(opt => {
      const btn = document.createElement("button");
      btn.className = "chip";
      btn.textContent = opt.label;
      btn.addEventListener("click", () => chooseOption(opt));
      suggestEl.appendChild(btn);
    });
  }

  function setSuggestLoading(isLoading){
    if (isLoading){
      suggestEl.innerHTML = `
        <div class="skeleton"></div>
        <div class="skeleton" style="width:140px"></div>
        <div class="skeleton" style="width:90px"></div>
      `;
    }
  }

  function disableSuggestions(disabled){
    [...suggestEl.querySelectorAll("button")].forEach(b => b.disabled = disabled);
  }

  /* ====== Send (typed) ====== */
  document.getElementById("gm-send").addEventListener("click", sendMessage);
  inputEl.addEventListener("keydown", (e) => {
    if (e.key === "Enter") sendMessage();
  });

  async function sendMessage(){
    const text = inputEl.value.trim();
    if (!text) return;
    addUser(text);
    inputEl.value = "";

    disableUI(true);
    const stopTyping = addTyping();
    try{
      const res = await fetch("/api/chat/message", {
        method: "POST",
        headers: { "Content-Type":"application/json" },
        body: JSON.stringify({ sessionId: getSessionId(), message: text })
      });
      const data = await res.json(); // { reply, options? }
      stopTyping();
      if (data?.message) addBot(data.message);
      if (Array.isArray(data?.options)) renderSuggestions(data.options);
    }catch(e){
      stopTyping();
     addBot("Sorry, I hit a snag. Please try again.");
    }finally{
      disableUI(false);
      scrollBottom();
    }
  }

  /* ====== Send (preselected option) ====== */
  async function chooseOption(opt){
    addUser(opt.label);
    disableUI(true);
    const stopTyping = addTyping();
    try{
      const res = await fetch("/api/chat/message", {
        method: "POST",
        headers: { "Content-Type":"application/json" },
        body: JSON.stringify({ sessionId: getSessionId(), message: null, choiceId: opt.id })
      });
      const data = await res.json(); // { reply, options? }
      stopTyping();
      if (data?.message) addBot(data.message);
      if (Array.isArray(data?.options)) renderSuggestions(data.options);
    }catch(e){
      stopTyping();
      addBot("Hmm, that didn’t work. Try again or type your question.");
    }finally{
      disableUI(false);
      scrollBottom();
    }
  }

  /* ====== Restart ====== */
  async function restart(){
    // Optional: tell backend to reset too:
    // try { await fetch(`/api/chat/restart?sessionId=${encodeURIComponent(getSessionId())}`, { method:"POST" }); } catch{}
    ensureSession(true);                 // new session id
    bodyEl.innerHTML = "";               // clear messages
    suggestEl.innerHTML = "";            // clear chips
    delete suggestEl.dataset.ready;
    inputEl.value = "";
    await boot();
  }

  /* ====== UI helpers ====== */
  function addBot(text){
    const block = document.createElement("div");
    block.className = "msg bot";
    block.innerHTML = `
      <div class="bubble">${escapeHtml(text)}</div>
      <div class="meta">Geekmonkey</div>
    `;
    bodyEl.appendChild(block);
    scrollBottom();
  }

  function addUser(text){
    const block = document.createElement("div");
    block.className = "msg me";
    block.innerHTML = `
      <div class="bubble">${escapeHtml(text)}</div>
      <div class="meta">You</div>
    `;
    bodyEl.appendChild(block);
    scrollBottom();
  }

  function addTyping(){
    const block = document.createElement("div");
    block.className = "msg bot";
    block.innerHTML = `<div class="bubble"><span class="typing"><span class="dot"></span><span class="dot"></span><span class="dot"></span></span></div>`;
    bodyEl.appendChild(block);
    scrollBottom();
    return () => block.remove();
  }

  function scrollBottom(){
    bodyEl.scrollTop = bodyEl.scrollHeight;
  }

  function disableUI(disabled){
    disableSuggestions(disabled);
    inputEl.disabled = disabled;
    document.getElementById("gm-send").disabled = disabled;
  }

  function escapeHtml(s){
    return String(s ?? "").replace(/[&<>"']/g, m => (
      {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#039;'}[m]
    ));
  }

  // Auto-open after short delay? Uncomment:
  // setTimeout(() => { fab.click(); }, 800);
});
