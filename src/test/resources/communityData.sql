INSERT INTO public.account (user_id, email, first_name, last_name, coordinates, last_location_update, image, hash)
VALUES ('202255', 'test13@email.com', 'test', 'user', ST_GeomFromText('POINT(-73.935242 40.730610)'), '2023-12-03', null, 'Ge7Y9frKWdgKcAysHdYCIoOOsAcn9We3f2+C74xlc6kWQZn2scBE8sEf4iZezwsmG/KdeeEuspZD9Q4Ojt27Hg==');
INSERT INTO public.account (user_id, email, first_name, last_name, coordinates, last_location_update, image, hash)
VALUES ('1', 'test14@email.com', 'test', 'testesen', ST_GeomFromText('POINT(-73.935242 40.730610)'), '2023-12-03', 'ok', 'Ge7Y9frKWdgKcAysHdYCIoOOsAcn9We3f2+C74xlc6kWQZn2scBE8sEf4iZezwsmG/KdeeEuspZD9Q4Ojt27Hg==');
INSERT INTO public.account (user_id, email, first_name, last_name, coordinates, last_location_update, image, hash)
VALUES ('3034', 'fake@user.com', 'fake', 'user', ST_GeomFromText('POINT(-73.935242 40.730610)'), '2023-12-03', 'ok', 'Ge7Y9frKWdgKcAysHdYCIoOOsAcn9We3f2+C74xlc6kWQZn2scBE8sEf4iZezwsmG/KdeeEuspZD9Q4Ojt27Hg==');

INSERT INTO public.community(community_id, description, location, name, image, visibility)
VALUES ('1000', 'En hyggelig dag', 'Storvold', 'Vi som liker været', null, 2);

INSERT INTO public.community(community_id, description, location, name, image, visibility)
VALUES ('9999', 'En hyggelig dag', 'Storvold', 'Vi som liker været', null, 1);

INSERT INTO public.community(community_id, description, location, name, image, visibility)
VALUES ('8888', 'En hyggelig dag', 'Storvold', 'Vi som liker været', null, 0);

INSERT INTO public.community(community_id, description, location, name, image, visibility)
VALUES ('1001', 'Fisk for folk', 'Ravnkloa', 'Det regner fisk', 'image', 1);

INSERT INTO public.user_community(community_id, user_id, is_administrator)
VALUES ('1001', '1', false);

INSERT INTO public.user_community(community_id, user_id, is_administrator)
VALUES ('1001', '202255', false);

INSERT INTO public.community(community_id, name, description, visibility, location, image)
VALUES ('4001', 'Haakon MC klubb', 'vi som liker motorsykkel og heter Haakon', 0, 'Elgeseter gate(midt på natta)', null);

INSERT INTO public.community(community_id, name, description, visibility, location, image)
VALUES ('4002', 'Sander MC klubb', 'vi som liker motorsykkel og heter Sander', 0, 'Elgeseter gate(midt på natta)', 'bilde');

INSERT INTO public.community(community_id, name, description, visibility, location, image)
VALUES ('4444', 'Aleks MC klubb', 'vi som liker motorsykkel og heter Aleks', 2, 'Elgeseter gate(midt på natta)', null);





INSERT INTO public.community(community_id, name, description, visibility, location, image)
VALUES ('4000', 'Einars MC klubb', 'vi som liker motorsykkel og heter Einar', 0, 'Elgeseter gate(midt på natta)', 'bilde');

INSERT INTO public.user_community(community_id, user_id, is_administrator)
VALUES ('4000', '1', true);

INSERT INTO public.user_community(community_id, user_id, is_administrator)
VALUES ('9999', '202255', true);

INSERT INTO public.user_community(community_id, user_id, is_administrator)
VALUES ('4000', '202255', false);

INSERT INTO public.user_community(community_id, user_id, is_administrator)
VALUES ('4002', '202255', false);

INSERT INTO public.user_community(community_id, user_id, is_administrator)
VALUES ('4001', '1', true);

INSERT INTO public.user_community(community_id, user_id, is_administrator)
VALUES ('4444', '1', false);



INSERT INTO public.item(item_id, description, title, user_id, deleted)
VALUES ('5000', 'yo', 'get over here', '1', false);

INSERT INTO public.community_item(community_id, item_id) VALUES ('4444', '5000');
