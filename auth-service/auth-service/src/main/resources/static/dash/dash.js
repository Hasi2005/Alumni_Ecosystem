(function(){
  // Derive role from URL path: /test/<role>
  const role = (location.pathname.split('/').pop() || '').toLowerCase();
  // Highlight current page if any link contains the role name
  const links = document.querySelectorAll('.links a');
  links.forEach(a => {
    if (a.href && a.href.toLowerCase().includes(role)) {
      a.classList.add('active');
    }
  });
})();

