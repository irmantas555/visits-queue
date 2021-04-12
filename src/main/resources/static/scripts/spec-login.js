// var details = {
//     'email': 'neil.butler@irmantasm.lt',
//     'password': 'Neil123',
// };

// var formBody = [];
// for (var property in details) {
//   var encodedKey = encodeURIComponent(property);
//   var encodedValue = encodeURIComponent(details[property]);
//   formBody.push(encodedKey + "=" + encodedValue);
// }

// formBody = formBody.join("&");

// const satrt = fetch('http://localhost:8080/spacialist/login', {
//   method: 'POST',
//   headers: {
//     'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
//   },
//   body: formBody
// });

// performAction = () =>{
//     satrt();
// }

const myform = document.getElementById("cForm");
const email = document.getElementById("username");
const fname = document.getElementById("fname");
const lname = document.getElementById("lname");
const regInput = document.getElementById("regButton");

function sendHttpRequest(method, url, data) {
  return fetch(url, {
    method: method,
    body: JSON.stringify(data),
  }).then((response) => {
    return response.json();
  });
}

let addC = () => {
if(fname !== undefined && fname !== null){  
    let em = email.value;
    let fn = fname.value;
    let ln = lname.value;
  console.log(em);
  const resp = sendHttpRequest(
    "GET",
    "http://localhost:8080/customer/add?email=" +
      em +
      "&fname=" +
      fn +
      "&lname=" +
      ln
  );

  if (em) {
    resp.then((val) => {
      console.log("Val: " + val);
      setTimeout(function () {
        myform.submit();
      }, 2000);
    });
  }}
};

if(fname !== undefined && fname !== null){
    regInput.addEventListener('click', function(event){
        addC();
    });
}