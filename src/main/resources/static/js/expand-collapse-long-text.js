function expandCollapseLongText(uuid) {
  var dots = document.getElementById("dots-".concat(uuid));
  var moreText = document.getElementById("more-".concat(uuid));

  if (dots.style.display === "none") {
    dots.style.display = "inline";
    moreText.style.display = "none";
  } else {
    dots.style.display = "none";
    moreText.style.display = "inline";
  }
}
