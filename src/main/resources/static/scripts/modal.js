const addModal = document.getElementById("add-modal");
const backdrop = document.getElementById("backdrop");
const cancelButton = document.getElementById('modal-cancel')
const confirmButton = cancelButton.nextElementSibling;
const modalContent = addModal.querySelector(".modal__content");
let stringContent = "";



const toggleBackdrop = () => {
  backdrop.classList.toggle("visible");
};

const toggleModal = () => {
  modalContent.textContent = stringContent;
  addModal.classList.toggle("visible");
  toggleBackdrop();
};

const cancelHandler = () => {
  toggleModal();
  return "Ok";
};

const okHandler = () => {
  toggleModal();
  return "Cancel";
};

function getReply(){
  toggleModal();
    console.log(value);
    return value;
}

// cancelButton.addEventListener("click", () => "Cancel");
// confirmButton.addEventListener("click", () => "Ok");

// addModal.addEventListener("click", () => {
//   setTimeout(() => {
//     toggleModal(),
//     console.log('finished! ')
//   }, 2000)
// })

// window.confirm('Confrim? ')