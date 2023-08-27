const currentUrl = window.location.host;
console.log(currentUrl);

async function post(boby){
    const url = "http://" + currentUrl + "/product/";
    const response = await fetch(url, {
        method : "POST",
        body: boby,
        headers : {
            "Content-Type": "application/json"
        },
    });

    return response.json();
}

let createButton = document.querySelector("button#create");
createButton?.addEventListener("click", ()=>{
    // @ts-ignore
    let product_id = document.querySelector("#product_id")?.value;
    // @ts-ignore
    let product_name = document.querySelector("#product_name")?.value;
    // @ts-ignore
    let product_default_uom = document.querySelector("#product_uom")?.value;
    // @ts-ignore
    let active = document.querySelector("#active_yes")?.checked;

    const boby = JSON.stringify({
        product:{
            id: product_id,
            name : product_name,
            default_uom: product_default_uom,
            activity: active
        }
    });

    post(boby).then((data) =>{
        console.log(data)
    });
})

