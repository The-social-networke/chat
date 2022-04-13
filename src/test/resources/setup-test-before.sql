INSERT INTO chat_room (id, created_at) VALUES
    ('350c19d5-2905-4c6e-9e60-4bb74a53745e', now()),
    ('3157333e-d7b2-4735-9069-fbd2cbf8e9f1', now()),
    ('cf35f7d8-2672-4b16-9457-9a4206e65930', now());
INSERT INTO user__chat_room (chat_room_id, user_id) VALUES
    ('350c19d5-2905-4c6e-9e60-4bb74a53745e', '8a744b81-38fd-4fe1-a032-33836e7a0221'),
    ('350c19d5-2905-4c6e-9e60-4bb74a53745e', '55ab96d7-8a93-4ea3-9d9d-77500018ad4e'),
    ('3157333e-d7b2-4735-9069-fbd2cbf8e9f1', '8a744b81-38fd-4fe1-a032-33836e7a0221'),
    ('3157333e-d7b2-4735-9069-fbd2cbf8e9f1', 'f510077c-144d-4ade-bec6-6c8fd6913544'),
    ('cf35f7d8-2672-4b16-9457-9a4206e65930', 'd0d152b8-cffa-4112-b4bd-7e469f5754a4'),
    ('cf35f7d8-2672-4b16-9457-9a4206e65930', 'b045d3de-2093-432a-b903-4e1d6fd6f539');

INSERT INTO message (id, chat_room_id, user_id, text, sent_at) VALUES
    ('5b4e2e22-5630-4353-94e9-92794c7c6741',
     '350c19d5-2905-4c6e-9e60-4bb74a53745e',
     '8a744b81-38fd-4fe1-a032-33836e7a0221',
     'hello',
     '2022-03-22 19:10:07.220500');
INSERT INTO message (id, chat_room_id, user_id, text, sent_at) VALUES
    ('7c610c79-369c-42af-9d51-bb3bc0891065',
     '350c19d5-2905-4c6e-9e60-4bb74a53745e',
     '8a744b81-38fd-4fe1-a032-33836e7a0221',
     'how are you',
     '2022-03-22 19:11:07.220500');
INSERT INTO message (id, chat_room_id, user_id, text, sent_at) VALUES
    ('50f43dd9-35e4-4c00-bd3a-c7b26575b153',
     '350c19d5-2905-4c6e-9e60-4bb74a53745e',
     '55ab96d7-8a93-4ea3-9d9d-77500018ad4e',
     'great!',
     '2022-03-23 10:10:07.220500');

INSERT INTO message (id, chat_room_id, user_id, text, sent_at) VALUES
    ('69be2df7-0c33-4112-8ea7-e226f9fb1887',
     '3157333e-d7b2-4735-9069-fbd2cbf8e9f1',
     'f510077c-144d-4ade-bec6-6c8fd6913544',
     'test message',
     '2022-04-01 20:20:07.220500');
