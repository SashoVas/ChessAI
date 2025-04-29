const initialPosition = [
    ['♜', '♞', '♝', '♛', '♚', '♝', '♞', '♜'],
    ['♟', '♟', '♟', '♟', '♟', '♟', '♟', '♟'],
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
    ['♙', '♙', '♙', '♙', '♙', '♙', '♙', '♙'],
    ['♖', '♘', '♗', '♕', '♔', '♗', '♘', '♖'],
];

let draggedPiece = null;
let offsetX = 0;
let offsetY = 0;

function createChessboard() {
    const chessboard = document.getElementById('chessboard');
    
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

function handleMouseDown(e) {
    if (!e.target.classList.contains('piece')) return;
    
    draggedPiece = e.target;
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
    
    if(targetSquare.hasChildNodes()){
        targetSquare.removeChild(targetSquare.lastChild)
    }
    // Reset styles and move to new square
    draggedPiece.style.position = '';
    draggedPiece.style.width = '';
    draggedPiece.style.height = '';
    draggedPiece.style.left = '';
    draggedPiece.style.top = '';
    targetSquare.appendChild(draggedPiece);

    document.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('mouseup', handleMouseUp);
    draggedPiece = null;
}

document.addEventListener('DOMContentLoaded', () => {
    createChessboard();
    document.getElementById('chessboard').addEventListener('mousedown', handleMouseDown);
});