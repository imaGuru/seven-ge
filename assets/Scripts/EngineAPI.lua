Entity = {}
Entity.__index = Entity

function Entity.create()
  SevenGE.log("SCRIPTS","Creating entity")
  local entity = {}
  entity.components={}
  entity.id = -1
  setmetatable(entity,Entity)
  return entity
end

function Entity:addComponent(position, component)
	self.components[position] = component
	--SevenGE.log("SCRIPTS","Addcomponents" .. self.components[0].x)
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

SevenGE.log("SCRIPTS","EngineAPI parsed ")