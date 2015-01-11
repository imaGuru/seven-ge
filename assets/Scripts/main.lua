-- This table is indexed by coroutine and simply contains the time at which the coroutine
-- should be woken up.
local WAITING_ON_TIME = {}

-- Keep track of how long the game has been running.
local CURRENT_TIME = 0

function waitSeconds(seconds)
    -- Grab a reference to the current running coroutine.
    local co = coroutine.running()

    -- If co is nil, that means we're on the main process, which isn't a coroutine and can't yield
    -- assert(co ~= nil, "The main thread cannot wait!")

    -- Store the coroutine and its wakeup time in the WAITING_ON_TIME table
    local wakeupTime = CURRENT_TIME + seconds
    WAITING_ON_TIME[co] = wakeupTime

    -- And suspend the process
    return coroutine.yield(co)
end

function wakeUpWaitingThreads(deltaTime)
    -- This function should be called once per game logic update with the amount of time
    -- that has passed since it was last called
    CURRENT_TIME = CURRENT_TIME + deltaTime

    -- First, grab a list of the threads that need to be woken up. They'll need to be removed
    -- from the WAITING_ON_TIME table which we don't want to try and do while we're iterating
    -- through that table, hence the list.
    local threadsToWake = {}
    for co, wakeupTime in pairs(WAITING_ON_TIME) do
        if wakeupTime < CURRENT_TIME then
            table.insert(threadsToWake, co)
        end
    end

    -- Now wake them all up.
    for _, co in ipairs(threadsToWake) do
        WAITING_ON_TIME[co] = nil -- Setting a field to nil removes it from the table
        coroutine.resume(co)
    end
end

function runProcess(func)
    -- This function is just a quick wrapper to start a coroutine.
    local co = coroutine.create(func)
    return coroutine.resume(co)
end

-- And a function to demo it all:
runProcess(function ()
    waitSeconds(5)
    SevenGE.log("SCRIPTS","We've got a problem:(.")
    entity = Entity.create()
    entity:addComponent(0,{x=-100,y=-100,rotation=45})
    entity:addComponent(1,{scale=2,textureRegion="Hull4.png"})
    SevenGE.log("SCRIPTS","Trying to create entity")
    EntityManager.create(entity)
    SevenGE.log("SCRIPTS","Created entity with id " .. entity.id)
    waitSeconds(5)
    EntityManager.remove(entity)
    SevenGE.log("SCRIPTS","Ha! haaa! I removed that entity!")
    SevenGE.setFrameTime(16)
end)