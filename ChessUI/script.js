const initialPosition = [
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
];
let blackPieceInitials={
    "k":"♚",
    "q":"♛",
    "n":"♞",
    "r":"♜",
    "b":"♝",
    "p":"♟",

}
let whitePieceInitials={
    "K":"♔",
    "Q":"♕",
    "N":"♘",
    "R":"♖",
    "B":"♗",
    "P":"♙",
}
const jwtToken = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXNobzMiLCJpYXQiOjE3NDY1NDIxMTYsImV4cCI6MTc2MjA5NDExNn0.Oku93E1p2ivhrGAgRFVuIN6Qsgz_w4yDMmF4Y8DPbn_I8Q0lss4amnHSp1GVw72jA0m0Esck6WleGrmcspGnqg'; 

const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);
let currentRoomId="1"
let draggedPiece = null;
let offsetX = 0;
let offsetY = 0;
let from;
let to;
let possibleMoves=[];
let firstBoardColor="#f0d9b5";
let secondBoardColor="#b58863";
let highlightColor='#a9a9a9';
let isBotMode=true;
let botEndpoint='/app/game.makeMoveToBot';
let multiplayerEndpoint='/app/game.makeMoveToPlayer';
let stompMessageHeaders={
    Authorization: `Bearer ${jwtToken}`,
    'heart-beat': '10000,10000'
}
let currentColor="";
function toChessAlgebraMove(move){
    var row=8-Math.floor(move/8);
    var col=move%8;
    return String.fromCharCode('a'.charCodeAt(0)+col) + row
}
function fromAlgebraToPosition(move){
    return 8*(8 - (move.charCodeAt(1) - '0'.charCodeAt(0))) + (move.charCodeAt(0) - 'a'.charCodeAt(0))
}
function toChessAlgebraMoveWhenBlack(move){
    var row=Math.floor(move/8)+1;
    var col=move%8;
    return String.fromCharCode('a'.charCodeAt(0)+col) + row
}
function fromAlgebraToPositionWhenBlack(move){
    return 8*((move.charCodeAt(1) - '0'.charCodeAt(0))-1) + (move.charCodeAt(0) - 'a'.charCodeAt(0))
}   
function getAttackedPositions(){
    if(currentColor=="WHITE"){

        return possibleMoves
        .filter((possibleMove)=>possibleMove.startsWith(toChessAlgebraMove(from)))
        .map((possibleMove)=>fromAlgebraToPosition(possibleMove.substring(2, 4)));
    }
    else{
        return possibleMoves
        .filter((possibleMove)=>possibleMove.startsWith(toChessAlgebraMoveWhenBlack(from)))
        .map((possibleMove)=>fromAlgebraToPositionWhenBlack(possibleMove.substring(2, 4)));
    }
}
function highlightPosition(pos){
    const square = document.getElementById('chessboard').children[pos];
    square.style.backgroundColor = highlightColor;
    square.style.borderColor = "black";
    square.style.borderRadius="10px";

}
function unHighlightPosition(pos){
    const square = document.getElementById('chessboard').children[pos];
    var evenPosColor=firstBoardColor;
    var oddPosColor=secondBoardColor;

    if(pos<8 || (pos>=16 && pos<24)|| (pos>=32 && pos<40)|| (pos>=48 && pos<56)){
        var evenPosColor=secondBoardColor;
        var oddPosColor=firstBoardColor;
    }
    if(pos%2==0){
        square.style.backgroundColor = evenPosColor;
    }
    else{
        square.style.backgroundColor = oddPosColor;
    }
    square.style.borderColor = "";
    square.style.borderRadius="0px";
}
function getSquarePos(element){
    const chessboard = document.getElementById('chessboard');
    return Array.prototype.indexOf.call(chessboard.children, element);
}
function emptyBoard(){
    for(let i=0;i<8;i++){
        for(let j=0;j<8;j++){
            initialPosition[i][j]='';
        }
    }
}
function getElementByPos(pos){
    const chessboard = document.getElementById('chessboard');
    return chessboard.children[pos]
}
function fenToBoard(fen){
    emptyBoard()
    let currentIndex=0;
    let boardFen=fen.split(" ")[0]
    for(let i=0;i<boardFen.length;i++){
        var currentLetter=boardFen[i]
        var convertDict=blackPieceInitials;
        if(currentLetter=='/')continue;
        if(currentLetter>='0' && currentLetter<='9'){
            currentIndex+=currentLetter - '0';
            continue;
        }
        else if(currentLetter==currentLetter.toUpperCase()){
            convertDict=whitePieceInitials;
        }
        var row=Math.floor(currentIndex/8);
        var col=currentIndex%8;

        initialPosition[row][col]=convertDict[currentLetter];
        
        currentIndex++;
    }

}

function createChessboard() {
    const chessboard = document.getElementById('chessboard');
    chessboard.innerHTML=''
    for (let row = 0; row < 8; row++) {
        for (let col = 0; col < 8; col++) {
            const square = document.createElement('div');
            square.className = 'square';
            chessboard.appendChild(square);
            var piece;
            if(currentColor=="BLACK"){
                piece=initialPosition[7-row][col];
    
            }
            else{
                piece = initialPosition[row][col];

            }
            if (piece) {
                const pieceElement = document.createElement('div');
                pieceElement.className = 'piece';
                pieceElement.textContent = piece;
                square.appendChild(pieceElement);
            }
        }
    }
}

function loadFen(fen){
    fenToBoard(fen)
    createChessboard()
}

function handleMouseDown(e) {
    if (!e.target.classList.contains('piece')) return;
    
    draggedPiece = e.target;
    from=getSquarePos(draggedPiece.parentNode)//The position of the start square

    const rect = draggedPiece.getBoundingClientRect();
    const chessboardRect = document.getElementById('chessboard').getBoundingClientRect();
    
    offsetX = e.clientX - rect.left;
    offsetY = e.clientY - rect.top;

    draggedPiece.style.position = 'absolute';
    draggedPiece.style.width = '60px';
    draggedPiece.style.height = '60px';
    document.getElementById('chessboard').appendChild(draggedPiece);

    const x = e.clientX - chessboardRect.left - offsetX;
    const y = e.clientY - chessboardRect.top - offsetY;
    draggedPiece.style.left = `${x}px`;
    draggedPiece.style.top = `${y}px`;
    draggedPiece.style.cursor=`-webkit-grabbing`
    draggedPiece.style.cursor=`grabbing`
    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);

    getAttackedPositions().forEach((pos)=>highlightPosition(pos));
}

function handleMouseMove(e) {
    if (!draggedPiece) return;
    
    const chessboard = document.getElementById('chessboard');
    const chessboardRect = chessboard.getBoundingClientRect();
    
    let x = e.clientX - chessboardRect.left - offsetX;
    let y = e.clientY - chessboardRect.top - offsetY;

    // Keep piece within board boundaries
    x = Math.max(0, Math.min(x, chessboard.offsetWidth - 60));
    y = Math.max(0, Math.min(y, chessboard.offsetHeight - 60));

    draggedPiece.style.left = `${x}px`;
    draggedPiece.style.top = `${y}px`;

}

function handleMouseUp(e) {
    if (!draggedPiece) return;

    const chessboard = document.getElementById('chessboard');
    const chessboardRect = chessboard.getBoundingClientRect();
    
    const x = e.clientX - chessboardRect.left;
    const y = e.clientY - chessboardRect.top;

    const col = Math.floor(x / 60);
    const row = Math.floor(y / 60);
    const index = row * 8 + col;
    const targetSquare = chessboard.children[index];

    to=getSquarePos(targetSquare)//The position of the end square
    var isMovePossible=possibleMoves.includes((toChessAlgebraMove(from)+toChessAlgebraMove(to)))
    if(currentColor=="BLACK"){
        isMovePossible=possibleMoves.includes((toChessAlgebraMoveWhenBlack(from)+toChessAlgebraMoveWhenBlack(to)))
    }
    if(isMovePossible && targetSquare.hasChildNodes()){
        targetSquare.removeChild(targetSquare.lastChild)
    }
    // Reset styles and move to new square
    draggedPiece.style.position = '';
    draggedPiece.style.width = '';
    draggedPiece.style.height = '';
    draggedPiece.style.left = '';
    draggedPiece.style.top = '';
    draggedPiece.style.cursor= 'grab';

    if(isMovePossible)
        targetSquare.appendChild(draggedPiece);
    else{
        chessboard.children[from].appendChild(draggedPiece);
        draggedPiece.style.cursor= '';
    }

    document.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('mouseup', handleMouseUp);
    draggedPiece = null;

    getAttackedPositions().forEach((pos)=>unHighlightPosition(pos));
    if(isMovePossible && from!=to){
        if(currentColor=="BLACK"){
            sendMessage(toChessAlgebraMoveWhenBlack(from)+toChessAlgebraMoveWhenBlack(to),currentRoomId)
        }
        else{
            sendMessage(toChessAlgebraMove(from)+toChessAlgebraMove(to),currentRoomId)

        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    createChessboard();
    document.getElementById('chessboard').addEventListener('mousedown', handleMouseDown);
});

function sendMessage(move, roomId) {
    const moveObj = {
        move: move,
        roomId: roomId
    };

    stompClient.send(isBotMode?botEndpoint:multiplayerEndpoint , stompMessageHeaders, JSON.stringify(moveObj));
}

function initialConnect(roomId){
    const moveObj = {
        roomId: roomId
    };

    stompClient.send("/app/game.initialConnect", stompMessageHeaders, JSON.stringify(moveObj));
}

function loadFenButtonClick(){
    const input = document.getElementById('FEN');
    fen=input.value;
    loadFen(fen);
}
stompClient.connect(stompMessageHeaders, function (frame) {
    console.log('Connected: ' + frame);
});

function joinRoom(roomId){

    
    stompClient.subscribe('/room/game.' + roomId, function (messageOutput) {
        const message = JSON.parse(messageOutput.body);
        possibleMoves=message.nextMoves;
        loadFen(message.fen)
        console.log("Received message:", message);
    },stompMessageHeaders)
    initialConnect(roomId)
}
function joinRoomOnClick(){
    joinRoom(document.getElementById('Room').value)
}

function createRoom(){
    fetch("http://localhost:8080/createGame",
        {
            method: "POST",
            body: JSON
            .stringify
            ({
              gameType: "BOT",
              gameTimeSeconds: 60,
            }),
            headers: {
              "Content-type": "application/json",
              "Authorization":`Bearer ${jwtToken}`
            },
          })
            .then((response) => response.json())
            .then((json) => {
                console.log(json);
                currentRoomId=json["gameId"]
                currentColor=json["user1Color"]
                joinRoom(currentRoomId)
            })
            .then(joinRoom);
}