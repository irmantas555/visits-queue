const specVisitsList = document.getElementById("ulist");
const specId = specVisitsList.getAttribute("data-spec");
function sendHttpRequest(method, url, data) {
  return fetch(url, {
    method: method,
    body: JSON.stringify(data),
  }).then((response) => {
    return response.json();
  });
}

let myvisit = {
  visitId: 0,
  longTimestamp: 0,
  serial: "",
  visitTime: 0,
  timeLeft: "",
  specFullName: "",
  custFullName: "",
  intVisitStatus: 0,
};

const cancelEvent = (resButton) => {
  const id = resButton.target.getAttribute("data-visit");
  const sibling = resButton.target.closest('a');
  const listItem = resButton.target.closest("li");
  const value = resButton.target.textContent;
  let rez;
  switch(value){
    case "Cancel": {
      rez = window.confirm("Are you sure you want to cancel appointment?");
      break;
    }
    case "Started": {
      rez = window.confirm("Do you want to start appointment?");
      break;
    }
    case "Finished": {
      rez = window.confirm("Do you want to mark appointment as finished?");
      break;
    }
  }
  if (rez) {
    switch (value) {
      case "Cancel": {
        sendHttpRequest("GET", "http://78.63.114.206/visit/delete/" + id).then(r => {});
        listItem.remove();
        break;
      }
      case "Started": {
        sendHttpRequest(
          "GET",
          "http://78.63.114.206/visit/started/details?" +
            "visitId=" +
            id +
            "&specialistId=" +
            specId
        ).then(r => {});
        break;
      }
      case "Finished": {
        sendHttpRequest(
          "GET",
          "http://78.63.114.206/visit/finished/details?" +
            "visitId=" +
            id +
            "&specialistId=" +
            specId
        ).then(r => {});
        break;
      }
    }
  }
};

function deleteListEntry(id) {
  const listEntyToDelete = document.getElementById("list" + id);
  listEntyToDelete.remove();
}

specVisitsList.addEventListener("click", cancelEvent);

