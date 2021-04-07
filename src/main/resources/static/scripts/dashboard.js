const onlist = document.getElementById('split-list');
const es = new EventSource('http://localhost:8080/visits/sse');
const template = document.getElementById('li-temp');


// es.addEventListener('newlist', function(newlist){
//     console.log(newlist);
// })
//const lii = template.querySelector('li');

// private long visitId;
// private long longTimestamp;
// private String serial;
// private String visitTime;
// private String timeLeft;
// private String specFullName;
// private String custFullName;
// private int intVisitStatus;

es.onmessage = function(e){
    newpList = JSON.parse(e.data)
    console.log(newpList);
    const newUlItemL = document.createElement('ul');
    newUlItemL.className = 'left';
    const newUlItemR = document.createElement('ul');
    newUlItemL.className = 'right';
    newpList.forEach(element => {
        const newListItem = document.createElement('li');
        newListItem.className = 'card-li';
        const specItem = document.createElement('srong');
        specItem.className = 'my-cell';
        specItem.textContent = element.specFullName;
        const serialItem = document.createElement('srong');
        serialItem.className = 'my-cell';
        serialItem.textContent = element.serial;
        if(element.intVisitStatus == 1){
            newListItem.appendChild(specItem);
            newListItem.appendChild(serialItem);
            newUlItemL.appendChild(newListItem);
        } else {
            newListItem.appendChild(serialItem);
            newListItem.appendChild(specItem);
            newUlItemR.appendChild(newListItem);
        }
        while (onlist.firstChild) {
            //The list is LIVE so it will re-index each call
            onlist.removeChild(onlist.firstChild);
        }
        onlist.appendChild(newUlItemL);
        onlist.appendChild(newUlItemR);

    });

    // let htm = '';
    // strList.forEach(function(element) {
        // console.log(element)
        // htm += "<li>" + element.firstName + "</li>";
    // });
    // list.innerHTML = htm;
}