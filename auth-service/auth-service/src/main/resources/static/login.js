(function(){
  const year = document.getElementById('year');
  if (year) year.textContent = new Date().getFullYear();

  const params = new URLSearchParams(location.search);
  const msg = document.getElementById('msg');
  if (msg) {
    if (params.has('error')) {
      msg.textContent = 'Invalid username or password.';
    } else if (params.has('logout')) {
      msg.style.color = '#30c48d';
      msg.textContent = 'You have been signed out.';
    } else {
      msg.textContent = '';
    }
  }
})();

