var stompClient = null;
let lastMessageFromId = null;
let meId = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

async function connect() {
    let socket = new SockJS('/ws-chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, frame => {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/chat/messages', obj => {
            showLoadingMessage();
        });
    });
    await showLoadingMessage();
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendText() {
    stompClient.send("/app/chat/sendMessage", {}, JSON.stringify(
        {
            'text': $("#text").val(),
            'userId': $("#userId").val(),
            'chatRoomId' : $("#chatId").val()
        }
    ));
}

async function showLoadingMessage() {
    meId = $("#userId").val();
    let messages = await fetch("http://localhost:8081/chat/all-messages?userId=" + meId + "&chatId=" + $("#chatId").val())
        .then(response => response.json())
        .then(response => response.content);
    console.log(messages);
    let createdBlock = false;
    let lastMessageFromIdLoading = null;
    let text = "";
    let append = (str) => {
        text += str;
    }
    messages.forEach(message => {
        if (createdBlock === true && (lastMessageFromIdLoading === null || lastMessageFromIdLoading !== message["userId"])) {
            append("</div>");
            createdBlock = false;
        }
        if (lastMessageFromIdLoading === null || lastMessageFromIdLoading !== message["userId"]) {
            let username = message["userId"] === meId ? "Me" : "user" + message["userId"];
            append(
                "<div class=\"block_messages_from_person\">" +
                "<div class=\"sent_from\">" +
                username +
                "</div>"
            )
            createdBlock = true;
        }
        let isOwnStyle = meId === message["userId"] ? "message_own" : "message_not_own";
        console.log(meId + " == " + message["userId"] + " r = ")
        console.log(meId === message["userId"])
        append(
            "<div class=\"new_line\">" +
            "<div class=\"" + isOwnStyle + " message_block\">"
        )
        append(message.text);
        append(
            "<div class=\"sent_at\">" + message["sentAt"] + "</div>"
        )
        append(
            "</div>" +
            "</div>"
        )
        lastMessageFromIdLoading = message["userId"];
    })
    $("#messages_block").empty().append(text)
}

function showMessage(message) {
    $("#messages_block").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', e => {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendText(); });
});

// example
// <div className="block_messages_from_person">
//     <div className="new_line">
//         <div className="message_own message_block">
//             sda_me
//             <div className="sent_at">12.12.12</div>
//         </div>
//     </div>
// </div>
// <div className="block_messages_from_person">
//     <div className="sent_from">
//         Person
//     </div>
//     <div className="new_line">
//         <div className="message_not_own message_block">
//             sda_not_me
//             <div className="sent_at">12.12.12</div>
//         </div>
//     </div>
//     <div className="new_line">
//         <div className="message_not_own message_block">
//             sda_not_me
//             <div className="sent_at">12.12.13</div>
//         </div>
//     </div>
// </div>