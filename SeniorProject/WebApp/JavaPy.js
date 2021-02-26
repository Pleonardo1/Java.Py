window.onload = function ()  {

    document.getElementById("myFile").addEventListener("change", function() {

      var reader = new FileReader();
      reader.addEventListener('load', function() {
        document.getElementById('myTextArea').innerText = this.result;
      });
      reader.readAsText(document.getElementById('myFile').files[0]);

    });

    var button = document.getElementById("copyID"),
            input = document.getElementById("myTextArea2");

        button.addEventListener("click", function(event) {
            event.preventDefault();
            input.select();
            document.execCommand("copy");
        });

    /*function getOutput() {
        document.getElementById("myTextArea2").src = "./output.txt";
    }*/

}

function clearOutput() {
        document.getElementById("myTextArea2").value = "";
}