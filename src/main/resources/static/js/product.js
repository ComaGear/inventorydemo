// @ts-nocheck
const currentUrl = window.location.host;

async function updateMeasure(){
    // TODO handle job about post a new measurement check <option> 'add Meas' is valid to post.
    // or update some measuremnt with put
}

async function createListener(){

    updateMeasure();

    let product_id = document.querySelector("#product_id")?.value;
    let product_name = document.querySelector("#product_name")?.value;
    let product_default_uom = document.querySelector("#product_uom")?.value;
    let active = document.querySelector("#active_yes")?.checked;

    const boby = JSON.stringify({
        product:{
            id: product_id,
            name : product_name,
            default_uom: product_default_uom,
            activity: active
        }
    });

    const url = "http://" + currentUrl + "/product/";
    const response = await fetch(url, {
        method : "POST",
        body: boby,
        headers : {
            "Content-Type": "application/json"
        },
    });

    response.json().then((data) =>{
        console.log(data)
    });
}

// async function selectOnChange(){
//     let select_list = document.querySelector("select#measurement_list");

//     if(select_list.options[select_list.selectedIndex].text == "new Measure"){
//         let measurement_name = document.querySelector("measurement_name");
//         let measure = document.querySelector("measure");
//         let barcode = document.querySelector("barcode");

//         measurement_name.value = "";
//         measure.value = "";
//         barcode.value = "";
//     }
    
    // select_list?
// }

let relative_id = null;
let measure_uom_name = null;
let measure_size = null;
let barcode = null;
function openDialog(measure_element){
    // const measure_element = measure_edit.parentElement;
    product_id = document.querySelector("#product_id");
    relative_id = measure_element.querySelector(".relative_id");
    measure_uom_name = measure_element.querySelector(".measure_uom_name");
    measure_size = measure_element.querySelector(".measure_size");
    barcode = measure_element.querySelector(".barcode");

    let dialog = document.querySelector("#measure_edit_dialog");
    dialog.showModal();
    // dialog.setAttribute("style", "display:block");

    let measure_dialog_close = document.querySelector("#measure_dialog_close");
    measure_dialog_close.addEventListener("click", (e)=>{
        dialog.close();
    })

    dialog.addEventListener("close", (e)=>{
        edit_dialog_uom_name.disabled = false;
        edit_measure_relative_id.disabled = false;
    })

    let measure_dialog_save = document.querySelector("#measure_dialog_save");
    measure_dialog_save.addEventListener("click", (e)=>{

        measure_element.id = `measure_${edit_measure_relative_id.value}`;

        relative_id.innerHTML = edit_measure_relative_id.value;
        measure_uom_name.innerHTML = edit_dialog_uom_name.value;
        measure_size.innerHTML = edit_dialog_measure_size.value;
        barcode.innerHTML = edit_dialog_barcode.value;


        // operation of update measure

        measure_dialog_save.addEventListener("click", null);
        dialog.close();
    })

    let edit_dialog_product_id = document.querySelector("#edit_dialog_product_id");
    edit_dialog_product_id.value = product_id.value;
    let edit_dialog_uom_name = document.querySelector("#edit_dialog_uom_name");
    edit_dialog_uom_name.value = measure_uom_name.innerHTML;
    let edit_dialog_measure_size = document.querySelector("#edit_dialog_measure_size");
    edit_dialog_measure_size.value = measure_size.innerHTML;
    let edit_dialog_barcode = document.querySelector("#edit_dialog_barcode");
    edit_dialog_barcode.value = barcode.innerHTML;
    let edit_measure_relative_id = document.querySelector("#edit_measure_relative_id");
    edit_measure_relative_id.value = relative_id.innerHTML;

    // only one of them available to edit.
    if(edit_dialog_uom_name.value != ""){
        edit_measure_relative_id.disabled = true;
        
    } else if(edit_measure_relative_id.value != ""){
        // because uom is null, relative id able to set own value.
        edit_dialog_uom_name.disabled = true;
    }
    edit_dialog_uom_name.addEventListener("change", (e)=>{
        edit_measure_relative_id.value = `${product_id.value}-${edit_dialog_uom_name.value}`;
        edit_measure_relative_id.disabled=true;
    })
    edit_measure_relative_id.addEventListener("change", (e)=>{
        edit_dialog_uom_name.disabled=true;
    })
}


let measure_edit = document.querySelectorAll(".measure_edit");

function handle_measure_edit(edit){
    openDialog(edit.parentElement);    
}
measure_edit.forEach(edit =>{   
    edit.addEventListener("click", (e) =>{
        handle_measure_edit(e.target);
    });
})

// measure_edit.addEventListener("click", handle_measure_edit(e));

let create_measure_button = document.querySelector("#create_measure_button");
create_measure_button.addEventListener("click", (e)=>{
    let measure_list = document.querySelector("#measurement_list");

    let newMeasure = document.createElement("div");
    measure_list.appendChild(newMeasure);
    newMeasure.className = "row measure";

    let measure_uom_name = document.createElement("div");
    newMeasure.appendChild(measure_uom_name);
    measure_uom_name.className = "measure_uom_name";

    let measure_size = document.createElement("div");
    newMeasure.appendChild(measure_size);
    measure_size.className = "measure_size";

    let relative_id = document.createElement("div");
    newMeasure.appendChild(relative_id);
    relative_id.className = "relative_id";

    let barcode = document.createElement("div");
    newMeasure.appendChild(barcode);
    barcode.className = "barcode";

    let measure_edit = document.createElement("button");
    newMeasure.appendChild(measure_edit);
    measure_edit.className = "measure_edit tiny_button";
    measure_edit.textContent = "edit";
    measure_edit.addEventListener("click", (e)=>{
        handle_measure_edit(e.target);
    })

    openDialog(newMeasure);
})



let createButton = document.querySelector("button#create");
createButton?.addEventListener("click", createListener);
// let measurement_list = document.querySelector("#measurement_list");
// measurement_list?.addEventListener("change", selectOnChange);

