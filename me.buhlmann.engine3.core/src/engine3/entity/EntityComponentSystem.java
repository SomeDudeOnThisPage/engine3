package engine3.entity;

import engine3.Engine3;
import engine3.entity.api.*;
import engine3.entity.events.ComponentOperationEvent;
import engine3.event.EventBindings;
import engine3.events.SceneGraphModificationEvent;
import engine3.render.IRenderer;

import java.util.*;

@SuppressWarnings("unused")
public class EntityComponentSystem {
  private final Map<String, IEntity> entities;
  private final Map<Class<? extends IEntityCollection>, IEntityCollection> collections;
  private final Map<IEntitySystem.SystemPriority, List<IEntitySystem>> systems;
  private final Map<IRenderer.Stage, List<IRenderSystem<?>>> renderers;
  private final Set<IEntityScript> scripts;
  private final Queue<Operation> operations;

  private void mapEntityCollection(IEntity entity, IEntityCollection collection) {
    final Class<? extends EntityComponent>[] components = collection.components();
    if (components.length > 0) {
      boolean hasRequired = true;
      for (Class<? extends EntityComponent> listen : components) {
        if (!entity.hasComponent(listen)) {
          hasRequired = false;
          break;
        }
      }

      if (hasRequired) {
        System.out.println("added entity '" + entity.getIdentifier() + "' to collection '"  + collection + "'");
        collection.addEntity(entity);
      } else {
        collection.removeEntity(entity);
      }
    }
  }

  private void addCollection(IEntityCollection collection) {
    System.err.println("ADDING COLLECTION");
    this.collections.put(collection.getClass(), collection);

    if (collection instanceof IEntitySystem) {
      this.systems.get(((IEntitySystem) collection).priority()).add((IEntitySystem) collection);
    }

    if (collection instanceof IRenderSystem<?>) {
      this.renderers.get(((IRenderSystem<?>) collection).stage()).add((IRenderSystem<?>) collection);
    }

    // map all entities to this collection
    for (IEntity entity : this.entities.values()) {
      this.mapEntityCollection(entity, collection);
    }

    EventBindings.register(collection);
  }

  private void removeCollection(IEntityCollection collection) {
    this.collections.remove(collection.getClass());

    if (collection instanceof IEntitySystem) {
      this.systems.get(((IEntitySystem) collection).priority()).remove(collection);
    }

    if (collection instanceof IRenderSystem) {
      this.systems.get(((IRenderSystem) collection).stage()).remove(collection);
    }

    EventBindings.unregister(collection);
  }

  private void handleCollectionOperation(Operation.OnCollection operation) {
    switch (operation.type) {
      case ADD -> this.addCollection(operation.collection);
      case REMOVE -> this.removeCollection(operation.collection);
      case MAP -> throw new UnsupportedOperationException("map operation not supported for collections - use with entities instead.");
    }
  }

  private void handleEntityOperation(Operation.OnEntity operation) {
    final IEntity entity = operation.entity;
    switch (operation.type) {
      case ADD -> {
        this.entities.put(entity.getIdentifier(), entity);
        for (IEntityCollection collection : this.collections.values()) {
          this.mapEntityCollection(entity, collection);
        }

        if (entity instanceof EventBindings) {
          EventBindings.register((EventBindings) entity);
        }
      }
      case REMOVE -> {
        this.entities.remove(entity.getIdentifier());

        if (entity instanceof EventBindings) {
          EventBindings.unregister((EventBindings) entity);
        }
      }
      case MAP -> {
        System.out.println("(re-)mapping entity '" + entity.getIdentifier() + "'");

        if (entity instanceof Entity) {
          ((Entity) entity).update();
        }

        for (IEntityCollection collection : this.collections.values()) {
          this.mapEntityCollection(entity, collection);
        }
      }
    }
  }

  public <T extends IEntityCollection> void add(T collection) {
    this.operations.add(new Operation.OnCollection(collection, Operation.Type.ADD));
  }

  public <T extends EntityCollection> void remove(T collection) {
    this.operations.add(new Operation.OnCollection(collection, Operation.Type.REMOVE));
  }

  public void add(IEntity entity) {
    this.operations.add(new Operation.OnEntity(entity, Operation.Type.ADD));
    this.operations.add(new Operation.OnEntity(entity, Operation.Type.MAP));
  }

  public void remove(IEntity entity) {
    this.operations.add(new Operation.OnEntity(entity, Operation.Type.REMOVE));
  }

  public IEntity get(String id) {
    if (!this.entities.containsKey(id)) {
      throw new UnsupportedOperationException("entity with id '" + id + "' not found");
    }

    return this.entities.get(id);
  }

  public <T extends EntityCollection> T get(T collection) {
    return null;
  }

  public void update(float dt) {
    while (!this.operations.isEmpty()) {
      final Operation operation = this.operations.poll();
      if (Operation.OnEntity.class == operation.getClass()) {
        this.handleEntityOperation((Operation.OnEntity) operation);
      } else if (Operation.OnCollection.class == operation.getClass()) {
        this.handleCollectionOperation((Operation.OnCollection) operation);
      }
    }

    for (IEntitySystem.SystemPriority priority : this.systems.keySet()) {
      for (IEntitySystem system : this.systems.get(priority)) {
        system.update(dt);
      }
    }

    for (IEntityScript script : this.scripts) {
      script.update(dt);
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends IRenderer> void updateRenderStage(IRenderer.Stage stage, T renderer) {
    for (IRenderSystem<?> system : this.renderers.get(stage)) {
      try {
        ((IRenderSystem<T>) system).render(renderer);
      } catch(ClassCastException e) {
        // todo: notify user
      }
    }
  }

  public EntityComponentSystem() {
    this.entities = new HashMap<>();
    this.collections = new HashMap<>();
    this.scripts = new HashSet<>();
    this.operations = new LinkedList<>();

    this.systems = new HashMap<>();
    for (IEntitySystem.SystemPriority priority : IEntitySystem.SystemPriority.values()) {
      this.systems.put(priority, new ArrayList<>());
    }

    this.renderers = new HashMap<>();
    for (IRenderer.Stage stage : IRenderer.Stage.values()) {
      this.renderers.put(stage, new ArrayList<>());
    }

    // fired when entity component set is changed
    Engine3.EVENT_BUS.subscribe(ComponentOperationEvent.class, (event) -> {
      // todo: this may result in ccmodexceptions!
      if (event.component instanceof IEntityScript script) {
        script.setEntity(event.entity);
        this.scripts.add(script);
      }

      if (this.entities.containsKey(event.entity.getIdentifier())) {
        if (event.entity.isFlagged(IEntity.Flags.COMPONENT_CHANGE)) {
          event.entity.unsetFlag(IEntity.Flags.COMPONENT_CHANGE);
          this.operations.add(new Operation.OnEntity(event.entity, Operation.Type.MAP));
        }
      }
    });

    // fired when entity is added as child (indirect addition to ecs via scenegraph)
    Engine3.EVENT_BUS.subscribe(SceneGraphModificationEvent.class, (event -> {
      if (!this.entities.containsKey(event.child.getIdentifier())) {
        this.add(event.child);
      }
    }));
  }
}
