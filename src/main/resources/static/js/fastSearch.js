

async function indexSuggest(name){
    const level_suggests = await loadSuggests();
    let suggests = [];

    const array = name.split(" ");

    for(const level in level_suggests){
        const cur = level_suggests[level];

        for(const key in cur){
            let substring = key.toLowerCase();
            let i = 0;
            let match = 0;
    
            while(i < array.length && !(substring.indexOf(array[i].toLowerCase()) < 0)){
    
                console.log("substring after cutted: " + substring);
                console.log("match index start of " + substring.indexOf(array[i].toLowerCase()));
    
                let subIndex = substring.indexOf(array[i].toLowerCase());
                substring = substring.substring(subIndex+array[i].length, substring.length);
                match++;
                i++;
            }
    
            if(i >= array.length && match >= array.length){
                suggests.push({
                    name : key,
                    id : cur[key]
                });
            }
        }
    }
    

    return suggests;
}

async function loadSuggests(){
    const response = await fetch("/suggest/get");
    const level_suggests = await response.json();
    return level_suggests;
}

document.querySelector("input#name_input_bar")?.addEventListener("input", update);

async function update(e){

    if(e.target.value != null){
        const list = await indexSuggest(e.target.value);

        console.log(list);

        let dropdown = document.querySelector("div#suggest_dropdown");
        if(dropdown != null) {
            dropdown.innerHTML = "";
        }
        for(let i = 0; i < list.length; i++){
            let v = list[i];
            
            let suggest_element = document.createElement("div");
            suggest_element.className = "suggest_element";

            let product_id = document.createElement("p");
            product_id.className = "suggest_product_id";
            product_id.innerText = v.id;

            let product_name = document.createElement("p");
            product_name.className = "suggest_name";
            product_name.innerText = v.name;

            suggest_element.addEventListener("click", goto);

            suggest_element.appendChild(product_id);
            suggest_element.appendChild(product_name);
            dropdown?.appendChild(suggest_element);
        }
    }
}

function goto(e){
    let suggest_product_id = e.currentTarget.querySelector("p.suggest_product_id").innerText;
    console.log(suggest_product_id);
    addMovement(suggest_product_id);
}

async function addMovement(product_id){

    let move_list = document.querySelector("div#move_list");
    let move_element = document.createElement("div");
    move_element.className = "move_element";

    let move_id = document.createElement("div");
    move_id.className = "move_id";

    let move_name = document.createElement("div");
    move_name.className = "move_name";

    move_element.appendChild(move_id);
    move_element.appendChild(move_name);
    move_list?.appendChild(move_element);


    let url = "/product/get/" + product_id;
    const response = await fetch(url);
    const json = response.json();

    json.then((json) =>{
        move_id.innerText = json.id;
        move_name.innerText = json.name;
    })
    
}