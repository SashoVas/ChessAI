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
let whitePieceInitials={
    "k":"♔",
    "q":"♕",
    "n":"♘",
    "r":"♖",
    "b":"♗",
    "p":"♙",

}
let blackPieceInitials={
    "K":"♚",
    "Q":"♛",
    "N":"♞",
    "R":"♜",
    "B":"♝",
    "P":"♟",
}
let draggedPiece = null;
let offsetX = 0;
let offsetY = 0;
let from;
let to;


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
function fenToBoard(fen){
    emptyBoard()
    let currentIndex=0;
    let boardFen=fen.split(" ")[0]
    for(let i=0;i<boardFen.length;i++){
        var currentLetter=boardFen[i]
        var convertDict=whitePieceInitials;
        if(currentLetter=='/')continue;
        if(currentLetter>='0' && currentLetter<='9'){
            currentIndex+=currentLetter - '0';
            continue;
        }
        else if(currentLetter==currentLetter.toUpperCase()){
            convertDict=blackPieceInitials;
        }
        var row=Math.floor(currentIndex/8);
        var col=currentIndex%8;

        initialPosition[7-row][col]=convertDict[currentLetter];
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
            
            const piece = initialPosition[row][col];
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
    if(targetSquare.hasChildNodes()){
        targetSquare.removeChild(targetSquare.lastChild)
    }
    // Reset styles and move to new square
    draggedPiece.style.position = '';
    draggedPiece.style.width = '';
    draggedPiece.style.height = '';
    draggedPiece.style.left = '';
    draggedPiece.style.top = '';
    draggedPiece.style.cursor= 'grab';
    targetSquare.appendChild(draggedPiece);

    document.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('mouseup', handleMouseUp);
    draggedPiece = null;
}

document.addEventListener('DOMContentLoaded', () => {
    createChessboard();
    document.getElementById('chessboard').addEventListener('mousedown', handleMouseDown);
});

const jwtToken = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzYXNobzMiLCJpYXQiOjE3NDYxODExMjUsImV4cCI6MTc2MTczMzEyNX0.mQGSjqG3WC0pbzFQMsClUf7qcLHNHLo-M-XMUgjrLXn5PA0F0wz5Mzh0mS_jnDsScr2vCblAbfTB_upjfoDMew'; 

const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({
    Authorization: `Bearer ${jwtToken}`,
    'heart-beat': '10000,10000'
}, function (frame) {
    console.log('Connected: ' + frame);

    const roomId = "room123";
    stompClient.subscribe('/room/game.' + roomId, function (messageOutput) {
        const message = JSON.parse(messageOutput.body);
        loadFen(message.fen)
        console.log("Received message:", message);
    });
});

function sendMessage(move, roomId) {
    const moveObj = {
        move: move,
        roomId: roomId
    };

    stompClient.send("/app/game.makeMove", {}, JSON.stringify(moveObj));
}

function initialConnect(roomId){
    const moveObj = {
        roomId: roomId
    };

    stompClient.send("/app/game.initialConnect", {}, JSON.stringify(moveObj));
}

function loadFenButtonClick(){
    const input = document.getElementById('FEN');
    fen=input.value;
    loadFen(fen);
}