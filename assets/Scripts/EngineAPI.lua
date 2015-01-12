Entity = {}
Entity.__index = Entity

function Entity.create()
  local entity = {}
  entity.components={}
  entity.id = -1
  setmetatable(entity,Entity)
  return entity
end

function Entity:addComponent(position, component)
	self.components[position] = component
end

function Entity:removeComponent(position)
  self.components[position] = nil
end

EntityManager = {}
EntityManager.__index = EntityManager

function EntityManager.create(entity)
  SevenGE.log("SCRIPTS","Registering entity")
	entity.id = SevenGE.createEntity(entity.components)
end

function EntityManager.remove(entity)
  SevenGE.removeEntity(entity.id)
end

function EntityManager.getEntity(id)
  SevenGE.getEntity(id)
end

Camera = {}
Camera.__index = Camera

function Camera.create()
  local camera = {}
  camera.x = 0
  camera.y = 0
  camera.rotation = 0
  camera.zoom = 1
  setmetatable(camera,Camera)
  return camera
end

function Camera.getCamera()
  local camera = {}
  camera.x, camera.y, camera.zoom, camera.rotation = SevenGE.getCamera()
  setmetatable(camera,Camera)
  return camera
end

function Camera:refresh()
  self.x, self.y, self.zoom, self.rotation = SevenGE.getCamera()
end

function Camera:setPosition(x,y)
  self.x = x 
  self.y = y 
  SevenGE.setCameraPosition(x,y)
end

function Camera:setRotation(angle)
  self.rotation = angle
  SevenGE.setCameraRotation(angle)
end

function Camera:setZoom(zoom)
  self.zoom = zoom
  SevenGE.setCameraZoom(zoom)
end

Audio = {}
Audio.__index = Audio

function Audio.setVolume(volume)
   SevenGE.setAudioVolume(volume)
end

function Audio.playSound(sound)
   SevenGE.playSound(sound)
end

function Audio.playMusic(sound)
   SevenGE.playMusic(sound)
end


SevenGE.log("SCRIPTS","EngineAPI parsed ")