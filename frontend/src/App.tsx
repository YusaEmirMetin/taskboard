import { useEffect, useRef, useState } from 'react'
import './App.css'
import { Client } from '@stomp/stompjs'

interface Task {
  id: number;
  title: string;
  description: string;
  status: string;
  userId?: number;
}

const STATUS_MAP = {
  TODO: 'YAPILACAK',
  IN_PROGRESS: 'DEVAM_EDIYOR',
  DONE: 'TAMAMLANDI'
};

function App() {
  const [tasks, setTasks] = useState<Task[]>([])
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [connected, setConnected] = useState(false)
  
  // --- EDİT MODU STATE'LERİ ---
  const [editingTaskId, setEditingTaskId] = useState<number | null>(null)
  const [editTitle, setEditTitle] = useState('')
  const [editDescription, setEditDescription] = useState('')
  
  const stompClient = useRef<Client | null>(null)

  const fetchTasks = () => {
    fetch('http://localhost:8080/api/tasks')
      .then(res => res.json())
      .then(data => setTasks(data))
      .catch(err => console.error("Backend'e ulaşılamadı:", err));
  }

  useEffect(() => {
    fetchTasks();
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws/websocket',
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true);
        client.subscribe('/topic/tasks', (message) => {
          const newTask: Task = JSON.parse(message.body);
          setTasks(prevTasks => {
            // Eğer status "DELETED" ise listeden tamamen çıkar
            if (newTask.status === 'DELETED') {
              return prevTasks.filter(t => t.id !== newTask.id);
            }
            // Değilse (Yeni veya Güncelleme), varsa güncelle yoksa ekle
            const filteredTasks = prevTasks.filter(t => t.id !== newTask.id);
            return [...filteredTasks, newTask];
          });
        });
      },
      onDisconnect: () => setConnected(false),
    });
    client.activate();
    stompClient.current = client;
    return () => { client.deactivate() };
  }, []);

  const deleteTask = (id: number) => {
    if (!window.confirm("Bu görevi silmek istediğine emin misin?")) return;
    
    fetch(`http://localhost:8080/api/tasks/${id}`, {
      method: 'DELETE'
    })
      .then(() => console.log("Görev silindi:", id))
      .catch(err => console.error("Silme hatası:", err));
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetch('http://localhost:8080/api/tasks', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, description, status: STATUS_MAP.TODO })
    })
      .then(res => res.json())
      .then(() => { setTitle(''); setDescription(''); })
      .catch(err => console.error("Görev eklenemedi:", err));
  }

  const updateTaskStatus = (task: Task, newStatus: string) => {
    fetch(`http://localhost:8080/api/tasks/${task.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...task, status: newStatus })
    })
      .then(res => res.json())
      .catch(err => console.error("Güncelleme hatası:", err));
  }

  // --- DÜZENLEME KAYDETME FONKSİYONU ---
  const handleSaveEdit = (task: Task) => {
    fetch(`http://localhost:8080/api/tasks/${task.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...task, title: editTitle, description: editDescription })
    })
      .then(res => res.json())
      .then(() => {
        setEditingTaskId(null);
      })
      .catch(err => console.error("Düzenleme hatası:", err));
  }

  const startEditing = (task: Task) => {
    setEditingTaskId(task.id);
    setEditTitle(task.title);
    setEditDescription(task.description);
  }

  const renderColumn = (title: string, status: string) => {
    const columnTasks = tasks.filter(t => t.status === status);
    return (
      <div className="column">
        <div className="column-header">
          <h3>{title}</h3>
          <span className="task-count">{columnTasks.length}</span>
        </div>
        <div className="tasks-list">
          {columnTasks.length === 0 ? (
            <div className="empty-state">Henüz görev yok</div>
          ) : (
            columnTasks.map(task => (
              <div key={task.id} className="task-card">
                {editingTaskId === task.id ? (
                  /* --- DÜZENLEME MODU GÖRÜNÜMÜ --- */
                  <div className="edit-mode">
                    <input 
                      className="edit-input" 
                      value={editTitle} 
                      onChange={e => setEditTitle(e.target.value)} 
                    />
                    <textarea 
                      className="edit-textarea" 
                      value={editDescription} 
                      onChange={e => setEditDescription(e.target.value)} 
                    />
                    <div className="edit-actions">
                      <button onClick={() => handleSaveEdit(task)} className="save-btn">Kaydet</button>
                      <button onClick={() => setEditingTaskId(null)} className="cancel-btn">İptal</button>
                    </div>
                  </div>
                ) : (
                  /* --- NORMAL GÖRÜNÜM --- */
                  <>
                    <div className="task-header">
                      <h4>{task.title}</h4>
                      <div className="task-header-btns">
                        <button onClick={() => startEditing(task)} className="edit-icon-btn">✎</button>
                        <button onClick={() => deleteTask(task.id)} className="delete-icon-btn">🗑</button>
                      </div>
                    </div>
                    <p className="description">{task.description || 'Açıklama yok'}</p>
                    <div className="task-footer">
                      <span className="user-id">#{task.id}</span>
                      <div className="task-actions">
                        {task.status === STATUS_MAP.TODO && (
                          <button onClick={() => updateTaskStatus(task, STATUS_MAP.IN_PROGRESS)} className="action-btn start">
                            Başlat →
                          </button>
                        )}
                        {task.status === STATUS_MAP.IN_PROGRESS && (
                          <button onClick={() => updateTaskStatus(task, STATUS_MAP.DONE)} className="action-btn complete">
                            Tamamla ✓
                          </button>
                        )}
                        {task.status === STATUS_MAP.DONE && (
                          <button onClick={() => updateTaskStatus(task, STATUS_MAP.TODO)} className="action-btn revert">
                            Geri Al
                          </button>
                        )}
                      </div>
                    </div>
                  </>
                )}
              </div>
            ))
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="app-container">
      <header className="header">
        <h1>TaskBoard</h1>
        <div className={`status-badge ${connected ? 'online' : ''}`}>
          {connected ? '● Canlı Bağlantı' : '○ Bağlantı Kuruluyor...'}
        </div>
      </header>

      <section className="form-card">
        <h3>✦ Hızlı Görev Ekle</h3>
        <form onSubmit={handleSubmit} className="form-row">
          <input
            className="form-input"
            placeholder="Ne yapılması gerekiyor?"
            value={title}
            onChange={e => setTitle(e.target.value)}
            required
          />
          <input
            className="form-input"
            placeholder="Detaylar (opsiyonel)"
            value={description}
            onChange={e => setDescription(e.target.value)}
          />
          <button type="submit" className="submit-btn">Ekle</button>
        </form>
      </section>

      <main className="board-grid">
        {renderColumn('Yapılacaklar', STATUS_MAP.TODO)}
        {renderColumn('Devam Ediyor', STATUS_MAP.IN_PROGRESS)}
        {renderColumn('Tamamlananlar', STATUS_MAP.DONE)}
      </main>
    </div>
  )
}

export default App
