runProcess(function ()
	Audio.playMusic("podbaydoors")
	for i=0,10 do
		waitSeconds(1)
		SevenGE.log("SCRIPTS","Creating space station "..i)
		entity = Entity.create()
		entity:addComponent(1,{-100,-100,45})
		entity:addComponent(2,{1,"starbase-tex.png"})
		entity:addComponent(5,{50,0.1,0.15});
		EntityManager.create(entity)
	end
	SevenGE.setFrameTime(16)
end)
