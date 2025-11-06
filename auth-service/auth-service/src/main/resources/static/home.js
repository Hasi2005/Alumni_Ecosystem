(function(){
  const burger = document.getElementById('burger');
  const links = document.getElementById('nav-links');
  const year = document.getElementById('year');

  if (year) year.textContent = new Date().getFullYear();

  if (burger && links) {
    burger.addEventListener('click', () => {
      const open = links.classList.toggle('open');
      burger.setAttribute('aria-expanded', String(open));
    });

    // Close the menu when clicking outside on small screens
    document.addEventListener('click', (e) => {
      if (!links.classList.contains('open')) return;
      const within = e.composedPath().some(el => el === links || el === burger);
      if (!within) links.classList.remove('open');
    });
  }
})();

