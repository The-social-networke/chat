var stompClient = null;
let meId = null;
let currentMessages = null;

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

function likeMessage(messageId,  isLike) {
    console.log(isLike);
    stompClient.send("/app/chat/likeMessage", {}, JSON.stringify(
        {
            'isLike': isLike,
            'userId': $("#userId").val(),
            'messageId' : messageId
        }
    ));
}

function readMessage(messageId) {
    stompClient.send("/app/chat/readMessage", {}, JSON.stringify(
        {
            'userId': $("#userId").val(),
            'messageId' : messageId
        }
    ));
}

function deleteMessage(messageId) {
    stompClient.send("/app/chat/deleteMessage", {}, JSON.stringify(
        {
            'userId': $("#userId").val(),
            'messageId' : messageId
        }
    ));
}

function showMessage(message) {
    $("#messages_block").append("<tr><td>" + message + "</td></tr>");
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
    currentMessages = messages;
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

        let isLiked = message.messageLikes.length === 0 ? "" : "color:red;";
        let isRead = message.messageReads.length === 0 ? "" : "border-style: dotted;";
        let style = "style=\" " + isLiked + isRead + " \"";

        append(
            "<div class=\"new_line\" id=\"" + message.id  + "\">" +
            "<div class=\"" + isOwnStyle + " message_block\" " + style + ">"
        )
        append(message.text);
        append(
            "<div class=\"sent_at\" style='font-size: 0.5em'>" + message["sentAt"].substring(0,19) + "</div>"
        )
        append(
            "</div>" +
            "</div>"
        )
        lastMessageFromIdLoading = message["userId"];
    })
    $("#messages_block").empty().append(text)
    currentMessages.forEach(message => {
        if(message.userId !== meId) {
            $("#" + message.id)
                .click(function () {
                    likeMessage(message.id, message.messageLikes.length === 0);
                })
                .mouseover(function () {
                    if(message.messageReads.length === 0) {
                        if (!message.messageReads.includes(meId)) {
                            readMessage(message.id);
                        }
                    }
                })
        }
        else {
            $("#" + message.id)
                .click(function () {
                    deleteMessage(message.id)
                })
        }
    })
}

$(function () {
    $("form").on('submit', e => {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendText(); });
});