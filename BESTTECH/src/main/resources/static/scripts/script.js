function updateFadeAndArrows() {
  const scroller = document.getElementById('scroll_items');
  const leftArrow = document.getElementById('items_left');
  const rightArrow = document.getElementById('items_right');

  // Pokazuj/ukrywaj strzałki na krawędziach slidera
  leftArrow.style.display = scroller.scrollLeft === 0 ? 'none' : 'block';
  rightArrow.style.display =
    Math.ceil(scroller.scrollLeft + scroller.clientWidth) >= scroller.scrollWidth
      ? 'none' : 'block';

  // Pokazuj/ukrywaj strzałki i litery "V" zależnie od szerokości ekranu
  if (window.innerWidth < 1220) {
    leftArrow.style.visibility = 'visible';
    rightArrow.style.visibility = 'visible';
    document.querySelectorAll('.v').forEach(el => el.style.visibility = 'visible');
  } else {
    leftArrow.style.visibility = 'hidden';
    rightArrow.style.visibility = 'hidden';
    document.querySelectorAll('.v').forEach(el => el.style.visibility = 'hidden');
  }
}

document.addEventListener("DOMContentLoaded", () => {
  document.getElementById('items_left').onclick = () =>
    document.getElementById('scroll_items').scrollBy({ left: -200, behavior: 'smooth' });
  document.getElementById('items_right').onclick = () =>
    document.getElementById('scroll_items').scrollBy({ left: 200, behavior: 'smooth' });

  document.getElementById('scroll_items').addEventListener('scroll', updateFadeAndArrows);
  window.addEventListener('resize', updateFadeAndArrows);
  updateFadeAndArrows();
});