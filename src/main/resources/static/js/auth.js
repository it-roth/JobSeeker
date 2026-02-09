// Central auth JS: login + register validation, email existence check, password toggles, custom select
(function(){
    const debounce = (fn, ms=350)=>{ let t; return (...args)=>{ clearTimeout(t); t = setTimeout(()=>fn(...args), ms) } };

    async function checkEmailExists(email){
        if(!email) return {exists:false};
        try{
            const res = await fetch('/api/check-email?email='+encodeURIComponent(email));
            if(!res.ok) return {exists:false};
            return await res.json();
        }catch(e){
            return {exists:false};
        }
    }

    /* Generic helper to show/hide error under an input */
    function setFieldError(input, msg){
        if(!input) return;
        // If the input is inside a password-wrapper (has a toggle), insert the error after the wrapper
        const pwWrapper = input.closest && input.closest('.password-wrapper');
        let referenceNode = pwWrapper || input;
        // prefer an error node immediately after the referenceNode, so each field has its own message
        let el = referenceNode.nextElementSibling;
        if(!(el && el.classList && el.classList.contains('field-error'))){
            el = document.createElement('div');
            el.className = 'field-error error';
            referenceNode.insertAdjacentElement('afterend', el);
        }
        el.textContent = msg || '';
        el.style.display = msg ? 'block' : 'none';
    }

    /* Password toggle: find any .password-wrapper and wire toggle buttons */
    function initPasswordToggles(){
        document.querySelectorAll('.password-wrapper').forEach(wrapper=>{
            const input = wrapper.querySelector('input[type="password"], input[type="text"]');
            const toggle = wrapper.querySelector('.password-toggle');
            if(!input || !toggle) return;
            toggle.addEventListener('click', ()=>{
                const hidden = input.type === 'password';
                input.type = hidden ? 'text' : 'password';
                toggle.setAttribute('aria-pressed', hidden ? 'true' : 'false');
                const eye = toggle.querySelector('.fa-eye');
                const eyeSlash = toggle.querySelector('.fa-eye-slash');
                if(hidden){ if(eye) eye.style.display='none'; if(eyeSlash) eyeSlash.style.display='inline-block'; }
                else { if(eye) eye.style.display='inline-block'; if(eyeSlash) eyeSlash.style.display='none'; }
            });
        });
    }

    /* Custom select behavior (register) - only initialize if present */
    function initCustomSelect(){
        const root = document.getElementById('customUserType');
        if(!root) return;
        const btn = root.querySelector('.custom-select-button');
        const items = Array.from(root.querySelectorAll('.custom-select-item'));
        const hidden = document.getElementById('userTypeInput');
        const selectedText = root.querySelector('.selected-text');
        const close = ()=>{ root.classList.remove('open'); btn.setAttribute('aria-expanded','false'); };
        const open = ()=>{ root.classList.add('open'); btn.setAttribute('aria-expanded','true'); };
        btn.addEventListener('click', e=>{ e.preventDefault(); root.classList.contains('open') ? close() : open(); });
        items.forEach(item=>{
            if(item.classList.contains('disabled')) return;
            item.addEventListener('click', function(){
                const val = this.getAttribute('data-value');
                const label = this.textContent.trim();
                hidden.value = val || '';
                items.forEach(i=>i.removeAttribute('aria-selected'));
                this.setAttribute('aria-selected','true');
                selectedText.textContent = label;
                // clear any validation error for account type
                setFieldError(root,'');
                close();
            });
        });
        document.addEventListener('click', e=>{ if(!root.contains(e.target)) close(); });
    }

    /* Login form validation */
    function initLogin(){
        const form = document.querySelector('form[action="/login"]');
        if(!form) return;
        const email = form.querySelector('#username');
        const pwd = form.querySelector('#password');
        const submit = form.querySelector('button[type="submit"]');

        const validateEmail = debounce(async function(){
            setFieldError(email,'');
            const val = email && email.value && email.value.trim();
            if(!val) return;
            const resp = await checkEmailExists(val);
            if(!resp.exists){ setFieldError(email,'No account is registered with this email.'); if(submit) submit.disabled=true; }
            else { setFieldError(email,''); if(submit) submit.disabled=false; }
        },300);

        const validatePassword = function(){
            setFieldError(pwd,'');
            if(!pwd) return true;
            if(!pwd.value || pwd.value.length === 0) return false;
            if(pwd.value.length < 8){ setFieldError(pwd,'Password must be at least 8 characters'); return false; }
            return true;
        };

        if(email) email.addEventListener('input', ()=>{ 
            if(submit) submit.disabled=false; 
            if(!email.value || !email.value.trim()){ setFieldError(email,''); return; }
            validateEmail(); 
        });
        // run once on load to catch browser autofill
        setTimeout(()=>{ if(email && email.value && email.value.trim()) validateEmail(); }, 250);
        if(pwd) pwd.addEventListener('input', ()=>{ if(!pwd.value){ setFieldError(pwd,''); return; } validatePassword(); });

        form.addEventListener('submit', async function(e){
            let ok = true;
            if(email){ const resp = await checkEmailExists(email.value.trim()); if(!resp.exists){ setFieldError(email,'No account is registered with this email.'); ok=false; } }
            if(!validatePassword()) ok=false;
            if(!ok) e.preventDefault();
        });
    }

    /* Register form validation */
    function initRegister(){
        const form = document.querySelector('form[action="/register"]');
        if(!form) return;
        const email = form.querySelector('#email');
        const pwd = form.querySelector('#password');
        const confirm = form.querySelector('#confirmPassword');
        const submit = form.querySelector('#submitBtn') || form.querySelector('button[type="submit"]');

        const emailCheck = debounce(async function(){
            setFieldError(email,'');
            const val = email && email.value && email.value.trim();
            if(!val) return;
            const resp = await checkEmailExists(val);
            if(resp.exists){ setFieldError(email,'Email already exists'); if(submit) submit.disabled=true; }
            else { setFieldError(email,''); if(submit) submit.disabled=false; }
        },350);

        const validatePasswords = function(){
            setFieldError(pwd,''); setFieldError(confirm,'');
            // if the user cleared the password, remove messages but still treat as invalid on submit
            if(pwd && (!pwd.value || pwd.value.length === 0)) return false;
            let ok = true;
            if(pwd && pwd.value.length < 8){ setFieldError(pwd,'Password must be at least 8 characters'); ok=false; }
            if(confirm && confirm.value && pwd && confirm.value !== pwd.value){ setFieldError(confirm,'Passwords do not match'); ok=false; }
            return ok;
        };

        if(email) email.addEventListener('input', ()=>{ 
            if(submit) submit.disabled=false; 
            if(!email.value || !email.value.trim()){ setFieldError(email,''); if(submit) submit.disabled=false; return; }
            emailCheck(); 
        });
        // run once on load to catch browser autofill
        setTimeout(()=>{ if(email && email.value && email.value.trim()) emailCheck(); }, 250);
        if(pwd) pwd.addEventListener('input', ()=>{ if(!pwd.value){ setFieldError(pwd,''); return; } validatePasswords(); });
        if(confirm) confirm.addEventListener('input', ()=>{ if(!confirm.value){ setFieldError(confirm,''); return; } validatePasswords(); });

        form.addEventListener('submit', async function(e){
            e.preventDefault();
            if(submit) submit.disabled = true;
            let ok = true;
            // email existence
            if(email){
                const resp = await checkEmailExists(email.value.trim());
                if(resp.exists){ setFieldError(email,'Email already exists'); ok=false; }
            }
            // passwords
            if(!validatePasswords()) ok=false;
            // account type
            const userTypeRoot = document.getElementById('customUserType');
            const userTypeHidden = document.getElementById('userTypeInput');
            if(!userTypeHidden || !userTypeHidden.value){
                if(userTypeRoot) setFieldError(userTypeRoot,'Please select account type');
                ok = false;
            }

            if(!ok){
                // focus first visible error
                if(submit) submit.disabled = false;
                const firstError = form.querySelector('.field-error');
                if(firstError){
                    const ref = firstError.previousElementSibling;
                    if(ref && typeof ref.focus === 'function') ref.focus();
                }
                return;
            }

            // all good -> submit the form natively
            form.submit();
        });
    }

    document.addEventListener('DOMContentLoaded', function(){
        initPasswordToggles();
        initCustomSelect();
        initLogin();
        initRegister();
    });

})();
