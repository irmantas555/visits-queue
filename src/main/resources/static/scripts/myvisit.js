function sendHttpRequest(method, url, data) {
  return fetch(url, {
    method: method,
    body: JSON.stringify(data),
  }).then((response) => {
    return response.json();
  });
}

function cancelEvent(eventId) {
  const resp = sendHttpRequest(
    "GET",
    "http://localhost:8080/visit/delete/" + eventId
  );
  resp.then((value) => {
    if (value.status == "Success") {
      deleteListEntry(eventId);
    }
  });
}

function deleteListEntry(id) {
  const listEntyToDelete = document.getElementById("list" + id);
  listEntyToDelete.remove();
}
