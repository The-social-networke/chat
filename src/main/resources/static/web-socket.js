let stompClient = null;
let meId = null;
let otherUserId = null;
let chatId = null;
let currentMessages = null;
let messageToChangeId = null;
let messageText = null;

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
    await init();
    await showLoadingMessage();
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

async function init() {
    meId = $("#userIdOne").val();
    otherUserId = $("#userIdTwo").val();
    let chat = await fetch("http://localhost:8081/chat/get-chat",
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                'users': [meId, otherUserId]
            })
        })
        .then(response => response.json())
    console.log(chat);
    chatId = chat.id;
    changeCurrentData()
}

function changeCurrentData() {
    $("#userIdOneShow").text(meId);
    $("#userIdTwoShow").text(otherUserId);
    $("#chatIdShow").text(chatId);
    $("#messageToChangeIdShow").text(messageToChangeId + " [" + messageText + "] ");
}

function sendText() {
    stompClient.send("/app/chat/sendMessage", {}, JSON.stringify(
        {
            'text': $("#text").val(),
            'userId': meId,
            'chatRoomId' : chatId
        }
    ));
}

function likeMessage(messageId,  isLike) {
    stompClient.send("/app/chat/likeMessage", {}, JSON.stringify(
        {
            'isLike': isLike,
            'userId': meId,
            'messageId' : messageId
        }
    ));
}

function readMessage(messageId) {
    stompClient.send("/app/chat/readMessage", {}, JSON.stringify(
        {
            'userId': meId,
            'messageId' : messageId
        }
    ));
}

function deleteMessage(messageId) {
    stompClient.send("/app/chat/deleteMessage", {}, JSON.stringify(
        {
            'userId': meId,
            'messageId' : messageId
        }
    ));
}
function updateMessage() {
    if(messageToChangeId === null) {
        alert("select message!")
        return;
    }
    stompClient.send("/app/chat/updateMessage", {}, JSON.stringify(
        {
            'text': $("#text").val(),
            'userId': meId,
            'messageId' : messageToChangeId
        }
    ));
}

async function showLoadingMessage() {
    console.log("meId        = " + meId);
    console.log("otherUserId = " + otherUserId);
    console.log("chatId      = " + chatId);
    let messages = await fetch("http://localhost:8081/chat/all-messages?userId=" + meId + "&chatId=" + chatId)
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
        append(message.text + "<br>");
        if(message.updated === true) {
            append("<b style='font-size: 0.5em;'>updated</b>")
        }
        append(
            "<div class=\"sent_at\" style=\"font-size: 0.5em\">" + message["sentAt"].substring(0,19) + "</div>"
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
                .mouseover(function () {
                    messageToChangeId = message.id
                    messageText = message.text
                    changeCurrentData()
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
    $( "#update" ).click(function() { updateMessage(); });
});