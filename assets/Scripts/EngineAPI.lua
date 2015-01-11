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

function Camera.setPosition(x,y)
  Camera.x, Camera.y = SevenGE.setCameraPosition(x,y)
end

function Camera.setRotation(angle)
  Camera.rotation = SevenGE.setCameraRotation(angle)
end

function Camera.setZoom(zoom)
  Camera.zoom = SevenGE.setCameraZoom(zoom)
end

Audio = {}
Audio.__index = Audio

function Audio.setVolume(volume)
   SevenGE.setAudioVolume(volume)
end

function Audio.setMusicLooping(loop)
   SevenGE.setMusicLooping(loop)
end

function Audio.playSound(sound)
   SevenGE.playSound(sound)
end

function Audio.playMusic(sound)
   Audio.currentTrack = SevenGE.playSound(sound)
end

function Audio.pause(sound)
   SevenGE.playSound(sound)
end

SevenGE.log("SCRIPTS","EngineAPI parsed ")