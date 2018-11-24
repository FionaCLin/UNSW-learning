

$('#next').click(function () {
  console.log("here")
})
$('#myCarousel').carousel({
  interval: 10000
})

var options = {
  enableHighAccuracy: true,
  timeout: 5000,
  maximumAge: 0
};


function success(pos) {
  var crd = pos.coords;
  long = document.getElementById('long')
  long.value = crd.longitude

  lati = document.getElementById('lati')
  lati.value = crd.latitude
  console.log('Your current position is:');
  console.log(`Latitude : ${crd.latitude}`);
  console.log(`Longitude: ${crd.longitude}`);
  console.log(`More or less ${crd.accuracy} meters.`);
};

function error(err) {
  console.warn(`ERROR(${err.code}): ${err.message}`);
};


navigator.geolocation.getCurrentPosition(success, error, options);
