let chatSocket = null;
let userSocket = null;
let meToken = null;
let meUserId = null;
let otherUserId = null;
let chatId = null;
let currentMessages = null;
let messageToChangeId = null;
let messageText = null;

//let port = "http://198.211.110.141:8002";
let port = "http://localhost:8081";

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
    meToken = $("#userToken").val();
    otherUserId = $("#userIdTwo").val();
    await setChat();
    let socket = new SockJS('/ws-chat', null, {});
    chatSocket = Stomp.over(socket);
    chatSocket.connect({
        'Authorization': 'Bearer ' + meToken
    }, frame => {
        setConnected(true);
        console.log('Connected: ' + frame);
        chatSocket.subscribe('/chat/messages/' + chatId, obj => {
            showLoadingMessage();
        })
    })
    let socket2 = new SockJS('/ws-chat', null, {});
    userSocket = Stomp.over(socket2);
    userSocket.connect({
        'Authorization': 'Bearer ' + meToken
    }, frame => {
        setConnected(true);
        console.log('Connected: ' + frame);
        chatSocket.subscribe('/users/' + meUserId, obj => {
            console.log(obj)
            console.log("YES");
        })
    })
    await changeCurrentData();
    await showLoadingMessage();
}

function disconnect() {
    if (chatSocket !== null) {
        chatSocket.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

async function setChat() {
    let chat = null;
    if(otherUserId) {
        chat = await fetch(port + "/chat/get-chat",
            {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + meToken,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    'userId': otherUserId
                })
            })
            .then(response => response.json())
    } else {
        chat = await fetch(port + "/chat/get-system-chat",
            {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + meToken,
                    'Content-Type': 'application/json',
                }
            })
            .then(response => response.json())
    }
    console.log(chat)
    chatId = chat.id
    meUserId = chat.users.filter(item => item !== $("#userIdTwo").val())
}

function changeCurrentData() {
    $("#userIdOneShow").text(meUserId);
    $("#userIdTwoShow").text(otherUserId);
    $("#chatIdShow").text(chatId);
    $("#messageToChangeIdShow").text(messageToChangeId + " [" + messageText + "] ");
}

function sendText() {
    chatSocket.send('/app/chat/sendMessage/' + chatId, {}, JSON.stringify(
        {
            'text': $("#text").val(),
            'chatRoomId': chatId
        }
    ));
}

function likeMessage(messageId,  isLike) {
    chatSocket.send('/app/chat/likeMessage/' + chatId, {}, JSON.stringify(
        {
            'isLike': isLike,
            'messageId' : messageId
        }
    ));
}

function readMessage(messageId) {
    chatSocket.send('/app/chat/readMessage/' + chatId, {}, JSON.stringify(
        {
            'messageId' : messageId
        }
    ));
}

function deleteMessage(messageId) {
    chatSocket.send('/app/chat/deleteMessage/' + chatId, {}, JSON.stringify(
        {
            'messageId' : messageId
        }
    ));
}

function updateMessage() {
    if(messageToChangeId === null) {
        alert("select message!")
        return;
    }
    chatSocket.send('/app/chat/updateMessage/' + chatId, {}, JSON.stringify(
        {
            'text': $("#text").val(),
            'messageId' : messageToChangeId
        }
    ));
}

async function showLoadingMessage() {
    // console.log("meUserId        = " + meUserId);
    // console.log("otherUserId = " + otherUserId);
    // console.log("chatId      = " + chatId);
    let messages = await fetch(port + "/chat/all-messages?chatId=" + chatId, {
        headers: {
            'Authorization': 'Bearer ' + meToken,
            'Content-Type': 'application/json',
        },
    })
        .then(response => response.json())
        .then(response => response.content);
    console.log(messages);
    console.log("meId" + meUserId)
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
            let username = message["userId"] == meUserId ? "Me" : "user " + message["userId"];
            append(
                "<div class=\"block_messages_from_person\">" +
                "<div class=\"sent_from\">" +
                username +
                "</div>"
            )
            createdBlock = true;
        }
        let isOwnStyle = meUserId == message["userId"] ? "message_own" : "message_not_own";

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
        if(message.userId != meUserId) {
            $("#" + message.id)
                .click(function () {
                    likeMessage(message.id, message.messageLikes.length === 0);
                })
                .mouseover(function () {
                    if(message.messageReads.length === 0) {
                        if (!message.messageReads.includes(meUserId)) {
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