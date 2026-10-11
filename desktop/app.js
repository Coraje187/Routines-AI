// Routines AI - Desktop & Web Engine (Genesis OS)
(function () {
  'use strict';

  // --- Audio Feedback (Web Audio API Synthesizer) ---
  const audioCtx = new (window.AudioContext || window.webkitAudioContext)();
  function playChime(success = true) {
    if (!audioCtx) return;
    try {
      if (audioCtx.state === 'suspended') audioCtx.resume();
      const now = audioCtx.currentTime;
      const osc = audioCtx.createOscillator();
      const gain = audioCtx.createGain();
      osc.connect(gain);
      gain.connect(audioCtx.destination);

      if (success) {
        osc.frequency.setValueAtTime(523.25, now); // C5
        osc.frequency.setValueAtTime(659.25, now + 0.1); // E5
        osc.frequency.setValueAtTime(783.99, now + 0.2); // G5
        gain.gain.setValueAtTime(0.25, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.45);
        osc.start(now);
        osc.stop(now + 0.45);
      } else {
        osc.frequency.setValueAtTime(440, now);
        gain.gain.setValueAtTime(0.15, now);
        gain.gain.exponentialRampToValueAtTime(0.001, now + 0.15);
        osc.start(now);
        osc.stop(now + 0.15);
      }
    } catch (e) {
      // Audio autoplay might be blocked before first user click
    }
  }

  // --- Initial / Seed Data ---
  const DEFAULT_DATA = {
    theme: 'dark',
    activeKidId: 1,
    kids: [
      { id: 1, name: 'Leo', avatar: '🦖', photo: null, points: 40 },
      { id: 2, name: 'Maya', avatar: '🦄', photo: null, points: 50 }
    ],
    routines: [
      { id: 1, kidId: 1, title: 'Put on comfortable socks', bucket: 'Morning', done: false },
      { id: 2, kidId: 1, title: 'Warm bath with dim lights', bucket: 'Bedtime', done: false },
      { id: 3, kidId: 1, title: 'Eat breakfast (oatmeal)', bucket: 'Morning', done: false },
      { id: 4, kidId: 1, title: 'Weighted blanket on bed', bucket: 'Bedtime', done: false },
      { id: 5, kidId: 1, title: 'Brush teeth (bubblegum paste)', bucket: 'Morning', done: false },
      { id: 6, kidId: 1, title: 'Pack sensory backpack', bucket: 'Morning', done: false },
      { id: 7, kidId: 2, title: 'Pack violin case', bucket: 'Morning', done: false },
      { id: 8, kidId: 2, title: 'Finish math homework', bucket: 'Afternoon', done: false },
      { id: 9, kidId: 2, title: 'Read 20 mins before bed', bucket: 'Bedtime', done: false }
    ],
    chores: [
      { id: 1, kidId: 1, title: 'Make bed with soft quilt', category: 'Bed', icon: '🛏️', points: 10 },
      { id: 2, kidId: 1, title: 'Put LEGOs in blue bin', category: 'Toys', icon: '🧸', points: 10 },
      { id: 3, kidId: 1, title: 'Feed the cat', category: 'Pet', icon: '🐱', points: 10 },
      { id: 4, kidId: 1, title: 'Clear plate to sink', category: 'Dishes', icon: '🍽️', points: 10 },
      { id: 5, kidId: 1, title: 'Sort laundry socks', category: 'Laundry', icon: '🧺', points: 10 },
      { id: 6, kidId: 1, title: '15 mins quiet reading', category: 'Reading', icon: '📚', points: 10 },
      { id: 7, kidId: 2, title: 'Walk the dog', category: 'Pet', icon: '🐶', points: 15 },
      { id: 8, kidId: 2, title: 'Empty dishwasher', category: 'Dishes', icon: '🍽️', points: 15 }
    ],
    events: [
      { id: 1, title: 'Occupational Therapy', date: '2026-10-14', time: '15:30', kidId: 1, category: 'Therapy' },
      { id: 2, title: 'School Fall Festival', date: '2026-10-18', time: '10:00', kidId: 1, category: 'School' },
      { id: 3, title: 'Soccer Practice', date: '2026-10-21', time: '16:00', kidId: 2, category: 'Activity' },
      { id: 4, title: 'Dental Cleaning Checkup', date: '2026-10-27', time: '09:00', kidId: 1, category: 'Medical' },
      { id: 5, title: 'Family Game Night', date: '2026-10-24', time: '18:30', kidId: 0, category: 'Family' }
    ],
    photos: [
      {
        id: 1,
        url: 'https://images.unsplash.com/photo-1511895426328-dc8714191300?auto=format&fit=crop&w=1200&q=80',
        caption: 'Family Mountain Hike & Picnic',
        date: 'Summer 2026'
      },
      {
        id: 2,
        url: 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?auto=format&fit=crop&w=1200&q=80',
        caption: 'Lake Cabin Adventures',
        date: 'Fall 2026'
      },
      {
        id: 3,
        url: 'https://images.unsplash.com/photo-1502086223501-7ea6ecd79368?auto=format&fit=crop&w=1200&q=80',
        caption: 'Sunny Beach Day with Sandcastles',
        date: 'Spring 2026'
      }
    ],
    notes: [
      'Leo responds best to First-Then cards when transitioning from screen time to bedtime.',
      'Sensory triggers for Leo: loud blending noises, itchy sock seams. Always turn socks inside out.',
      'Maya allergy reminder: Mild sesame sensitivity. Check bakery bread labels.',
      'Calm down protocol: Offer weighted lap pad, dim overhead lights, play low white noise.'
    ],
    weatherSettings: {
      city: 'Austin, Texas',
      lat: 30.2672,
      lon: -97.7431,
      unit: 'fahrenheit'
    }
  };

  // --- Storage & State Management ---
  const STORAGE_KEY = 'routines_ai_state_v1';
  let state = loadState();

  function loadState() {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) return JSON.parse(saved);
    } catch (e) {
      console.warn('Failed to load localStorage', e);
    }
    return JSON.parse(JSON.stringify(DEFAULT_DATA));
  }

  function saveState() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch (e) {
      console.error('Failed to save to localStorage', e);
    }
  }

  // --- DOM Elements ---
  const elKidSelector = document.getElementById('kidSelector');
  const elBtnAddKid = document.getElementById('btnAddKid');
  const elClockTime = document.getElementById('clockTime');
  const elClockDate = document.getElementById('clockDate');
  const elWeatherWidget = document.getElementById('weatherWidget');
  const elWeatherIcon = document.getElementById('weatherIcon');
  const elWeatherTemp = document.getElementById('weatherTemp');
  const elWeatherCity = document.getElementById('weatherCity');
  const elBtnThemeToggle = document.getElementById('btnThemeToggle');
  const elThemeIcon = document.getElementById('themeToggleIcon');

  const tabBtns = document.querySelectorAll('.tab-btn');
  const tabContents = document.querySelectorAll('.tab-content');

  // Routines & Chores
  const elRoutinesTitle = document.getElementById('routinesSectionTitle');
  const elChoresTitle = document.getElementById('choresSectionTitle');
  const elRoutineList = document.getElementById('routineList');
  const elChoreGrid = document.getElementById('choreGrid');
  const elStarSummaryText = document.getElementById('starSummaryText');
  const elFtFirstText = document.getElementById('ftFirstText');
  const elFtThenText = document.getElementById('ftThenText');

  // Calendar
  const elCalMonthTitle = document.getElementById('calMonthTitle');
  const elCalGrid = document.getElementById('calGrid');
  const elCalPrev = document.getElementById('calPrevMonth');
  const elCalNext = document.getElementById('calNextMonth');
  const elAgendaTitle = document.getElementById('agendaTitle');
  const elAgendaEvents = document.getElementById('agendaEvents');

  // Family Frame Gallery
  const elGalleryMainImg = document.getElementById('galleryMainImg');
  const elGalleryCaption = document.getElementById('galleryCaptionText');
  const elGalleryDate = document.getElementById('galleryDateText');
  const elGalleryThumbs = document.getElementById('galleryThumbs');
  const elBtnToggleSlideshow = document.getElementById('btnToggleSlideshow');
  const elAmbientPhotoImg = document.getElementById('ambientPhotoImg');
  const elAmbientCaption = document.getElementById('ambientCaption');
  const elAmbientSub = document.getElementById('ambientSub');

  // Right Pane Mode Switch
  const elModeAi = document.getElementById('modeAiBrain');
  const elModePhoto = document.getElementById('modePhotoFrame');
  const elViewAi = document.getElementById('viewAiBrain');
  const elViewPhoto = document.getElementById('viewPhotoFrame');
  const elAiSubtitle = document.getElementById('aiSubtitle');
  const elChatMessages = document.getElementById('chatMessages');
  const elAiInput = document.getElementById('aiInput');
  const elBtnAiSend = document.getElementById('btnAiSend');
  const elBtnVoiceLog = document.getElementById('btnVoiceLog');

  // Modals
  const modalAddKid = document.getElementById('modalAddKid');
  const modalEditKidPhoto = document.getElementById('modalEditKidPhoto');
  const modalAddRoutine = document.getElementById('modalAddRoutine');
  const modalAddChore = document.getElementById('modalAddChore');
  const modalAddEvent = document.getElementById('modalAddEvent');
  const modalWeather = document.getElementById('modalWeather');
  const modalAddPhoto = document.getElementById('modalAddPhoto');
  const modalVoiceNote = document.getElementById('modalVoiceNote');

  let currentCalDate = new Date();
  let selectedCalDay = new Date();
  let activeGalleryIndex = 0;
  let slideshowInterval = null;
  let editingKidId = null;

  // --- Theme Controller ---
  function applyTheme(theme) {
    state.theme = theme;
    if (theme === 'light') {
      document.body.classList.remove('dark-theme');
      document.body.classList.add('light-theme');
      elThemeIcon.textContent = '🌙';
    } else {
      document.body.classList.remove('light-theme');
      document.body.classList.add('dark-theme');
      elThemeIcon.textContent = '☀️';
    }
    saveState();
  }

  elBtnThemeToggle.addEventListener('click', () => {
    applyTheme(state.theme === 'dark' ? 'light' : 'dark');
    playChime(false);
  });

  // --- Clock Widget ---
  function updateClock() {
    const now = new Date();
    const timeStr = now.toLocaleTimeString([], { hour: 'numeric', minute: '2-digit', hour12: true });
    const dateStr = now.toLocaleDateString([], { weekday: 'short', month: 'short', day: 'numeric' });
    elClockTime.textContent = timeStr;
    elClockDate.textContent = dateStr;
  }
  setInterval(updateClock, 1000);
  updateClock();

  // --- Weather Service (Open-Meteo) ---
  async function fetchWeather() {
    const { lat, lon, city, unit } = state.weatherSettings;
    elWeatherCity.textContent = city;
    const tempUnitParam = unit === 'celsius' ? 'celsius' : 'fahrenheit';
    const tempSymbol = unit === 'celsius' ? '°C' : '°F';

    try {
      const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,weather_code&temperature_unit=${tempUnitParam}`;
      const res = await fetch(url);
      if (!res.ok) throw new Error('Weather API error');
      const data = await res.json();
      const temp = Math.round(data.current.temperature_2m);
      const code = data.current.weather_code;

      let icon = '☀️';
      if ([1, 2, 3].includes(code)) icon = '⛅';
      else if ([45, 48].includes(code)) icon = '🌫️';
      else if ([51, 53, 55, 61, 63, 65].includes(code)) icon = '🌧️';
      else if ([71, 73, 75, 77].includes(code)) icon = '❄️';
      else if ([95, 96, 99].includes(code)) icon = '⛈️';

      elWeatherIcon.textContent = icon;
      elWeatherTemp.textContent = `${temp}${tempSymbol}`;
    } catch (err) {
      console.warn('Weather fallback:', err);
      elWeatherIcon.textContent = '☀️';
      elWeatherTemp.textContent = unit === 'celsius' ? '22°C' : '72°F';
    }
  }
  fetchWeather();
  setInterval(fetchWeather, 15 * 60 * 1000);

  // Weather Modal
  elWeatherWidget.addEventListener('click', () => {
    document.getElementById('weatherInputCity').value = state.weatherSettings.city;
    document.getElementById('weatherInputLat').value = state.weatherSettings.lat;
    document.getElementById('weatherInputLon').value = state.weatherSettings.lon;
    document.getElementById('weatherInputUnit').value = state.weatherSettings.unit;
    modalWeather.showModal();
  });

  document.getElementById('btnSaveWeatherSettings').addEventListener('click', () => {
    state.weatherSettings.city = document.getElementById('weatherInputCity').value.trim() || 'Austin, Texas';
    state.weatherSettings.lat = parseFloat(document.getElementById('weatherInputLat').value) || 30.2672;
    state.weatherSettings.lon = parseFloat(document.getElementById('weatherInputLon').value) || -97.7431;
    state.weatherSettings.unit = document.getElementById('weatherInputUnit').value;
    saveState();
    modalWeather.close();
    fetchWeather();
    playChime(true);
  });

  // --- Kid Profile Switcher ---
  function getActiveKid() {
    return state.kids.find(k => k.id === state.activeKidId) || state.kids[0];
  }

  function renderKidSelector() {
    elKidSelector.innerHTML = '';
    state.kids.forEach(kid => {
      const isActive = kid.id === state.activeKidId;
      const pill = document.createElement('div');
      pill.className = `kid-pill ${isActive ? 'active' : ''}`;

      const avatarEl = document.createElement('div');
      avatarEl.className = 'kid-avatar';
      if (kid.photo) {
        avatarEl.innerHTML = `<img src="${kid.photo}" alt="${kid.name}">`;
      } else {
        avatarEl.textContent = kid.avatar || kid.name.charAt(0);
      }

      const nameEl = document.createElement('span');
      nameEl.textContent = kid.name;

      const starsEl = document.createElement('span');
      starsEl.className = 'kid-stars';
      starsEl.innerHTML = `★ ${kid.points} pts`;

      pill.appendChild(avatarEl);
      pill.appendChild(nameEl);
      pill.appendChild(starsEl);

      // Photo Edit Button
      const btnPhoto = document.createElement('button');
      btnPhoto.className = 'kid-action-btn';
      btnPhoto.title = 'Edit avatar or photo';
      btnPhoto.textContent = '📷';
      btnPhoto.addEventListener('click', (e) => {
        e.stopPropagation();
        openEditKidPhoto(kid.id);
      });
      pill.appendChild(btnPhoto);

      // Remove Kid Button (if more than 1 kid)
      if (state.kids.length > 1) {
        const btnRemove = document.createElement('button');
        btnRemove.className = 'kid-action-btn';
        btnRemove.title = 'Remove profile';
        btnRemove.textContent = '✕';
        btnRemove.addEventListener('click', (e) => {
          e.stopPropagation();
          if (confirm(`Remove profile for ${kid.name}? This will remove associated routines and chores.`)) {
            removeKid(kid.id);
          }
        });
        pill.appendChild(btnRemove);
      }

      pill.addEventListener('click', () => {
        if (state.activeKidId !== kid.id) {
          state.activeKidId = kid.id;
          saveState();
          renderAll();
          playChime(false);
        }
      });

      elKidSelector.appendChild(pill);
    });
  }

  function removeKid(kidId) {
    state.kids = state.kids.filter(k => k.id !== kidId);
    state.routines = state.routines.filter(r => r.kidId !== kidId);
    state.chores = state.chores.filter(c => c.kidId !== kidId);
    if (state.activeKidId === kidId) {
      state.activeKidId = state.kids[0].id;
    }
    saveState();
    renderAll();
  }

  // --- Add Kid Modal ---
  let selectedNewAvatar = '🦁';
  elBtnAddKid.addEventListener('click', () => {
    document.getElementById('newKidName').value = '';
    document.getElementById('newKidPhotoInput').value = '';
    modalAddKid.showModal();
  });

  document.querySelectorAll('#avatarGrid .avatar-option').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('#avatarGrid .avatar-option').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      selectedNewAvatar = btn.getAttribute('data-avatar');
    });
  });

  document.getElementById('btnSaveNewKid').addEventListener('click', () => {
    const name = document.getElementById('newKidName').value.trim();
    if (!name) return alert('Please enter a name for the child');

    const fileInput = document.getElementById('newKidPhotoInput');
    if (fileInput.files && fileInput.files[0]) {
      const reader = new FileReader();
      reader.onload = (e) => {
        createNewKidRecord(name, selectedNewAvatar, e.target.result);
      };
      reader.readAsDataURL(fileInput.files[0]);
    } else {
      createNewKidRecord(name, selectedNewAvatar, null);
    }
  });

  function createNewKidRecord(name, avatar, photo) {
    const newId = Date.now();
    const newKid = {
      id: newId,
      name,
      avatar,
      photo,
      points: 20
    };
    state.kids.push(newKid);
    state.activeKidId = newId;

    // Seed initial routine & chores for new kid
    state.routines.push(
      { id: Date.now() + 1, kidId: newId, title: 'Brush morning teeth', bucket: 'Morning', done: false },
      { id: Date.now() + 2, kidId: newId, title: 'Put shoes in closet', bucket: 'Bedtime', done: false }
    );
    state.chores.push(
      { id: Date.now() + 3, kidId: newId, title: 'Make bed', category: 'Bed', icon: '🛏️', points: 10 },
      { id: Date.now() + 4, kidId: newId, title: 'Put toys in basket', category: 'Toys', icon: '🧸', points: 10 }
    );

    saveState();
    modalAddKid.close();
    renderAll();
    playChime(true);
  }

  // --- Edit Kid Photo Modal ---
  let selectedEditAvatar = '🦁';
  function openEditKidPhoto(kidId) {
    editingKidId = kidId;
    const kid = state.kids.find(k => k.id === kidId);
    document.getElementById('editKidPhotoTitle').textContent = `Update Photo for ${kid.name}`;
    document.getElementById('editKidPhotoInput').value = '';
    modalEditKidPhoto.showModal();
  }

  document.querySelectorAll('#editAvatarGrid .avatar-option').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('#editAvatarGrid .avatar-option').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      selectedEditAvatar = btn.getAttribute('data-avatar');
    });
  });

  document.getElementById('btnSaveEditKidPhoto').addEventListener('click', () => {
    const kid = state.kids.find(k => k.id === editingKidId);
    if (!kid) return;

    const fileInput = document.getElementById('editKidPhotoInput');
    if (fileInput.files && fileInput.files[0]) {
      const reader = new FileReader();
      reader.onload = (e) => {
        kid.photo = e.target.result;
        saveState();
        modalEditKidPhoto.close();
        renderAll();
        playChime(true);
      };
      reader.readAsDataURL(fileInput.files[0]);
    } else {
      kid.avatar = selectedEditAvatar;
      kid.photo = null;
      saveState();
      modalEditKidPhoto.close();
      renderAll();
      playChime(true);
    }
  });

  // --- Tab Navigation ---
  tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const targetTab = btn.getAttribute('data-tab');
      tabBtns.forEach(b => b.classList.remove('active'));
      tabContents.forEach(c => c.classList.remove('active'));

      btn.classList.add('active');
      document.getElementById(`tab-${targetTab}`).classList.add('active');
      playChime(false);
    });
  });

  // --- Routines Rendering ---
  function renderRoutines() {
    const kid = getActiveKid();
    elRoutinesTitle.textContent = `${kid.name}'s Routines`;
    elAiSubtitle.textContent = `Ask how to prep meals, sensory quirks, calm-down routines, or rules for ${kid.name}...`;
    elAiInput.placeholder = `Ask about ${kid.name}...`;

    const kidRoutines = state.routines.filter(r => r.kidId === kid.id);

    // Update First-Then Card
    const uncompleted = kidRoutines.filter(r => !r.done);
    if (uncompleted.length >= 2) {
      elFtFirstText.textContent = uncompleted[0].title;
      elFtThenText.textContent = uncompleted[1].title;
    } else if (uncompleted.length === 1) {
      elFtFirstText.textContent = uncompleted[0].title;
      elFtThenText.textContent = 'Great job! Take a relaxing break';
    } else {
      elFtFirstText.textContent = 'All routines completed!';
      elFtThenText.textContent = '🌟 Free choice star time';
    }

    elRoutineList.innerHTML = '';
    kidRoutines.forEach(routine => {
      const item = document.createElement('div');
      item.className = `routine-item ${routine.done ? 'done' : ''}`;

      const info = document.createElement('div');
      info.className = 'routine-info';
      info.innerHTML = `
        <span class="routine-title">${routine.title}</span>
        <span class="routine-bucket">${routine.bucket}</span>
      `;

      const actions = document.createElement('div');
      actions.className = 'routine-actions';

      const btnDelete = document.createElement('button');
      btnDelete.className = 'btn-delete-item';
      btnDelete.title = 'Delete routine';
      btnDelete.innerHTML = '🗑️';
      btnDelete.addEventListener('click', (e) => {
        e.stopPropagation();
        if (confirm(`Delete routine "${routine.title}"?`)) {
          state.routines = state.routines.filter(r => r.id !== routine.id);
          saveState();
          renderRoutines();
        }
      });

      const checkbox = document.createElement('div');
      checkbox.className = 'routine-checkbox';
      checkbox.textContent = '✓';
      checkbox.addEventListener('click', () => {
        routine.done = !routine.done;
        if (routine.done) {
          kid.points += 5;
          playChime(true);
        } else {
          kid.points = Math.max(0, kid.points - 5);
          playChime(false);
        }
        saveState();
        renderAll();
      });

      actions.appendChild(btnDelete);
      actions.appendChild(checkbox);
      item.appendChild(info);
      item.appendChild(actions);

      elRoutineList.appendChild(item);
    });
  }

  // Add Routine Modal
  document.getElementById('btnAddRoutine').addEventListener('click', () => {
    document.getElementById('newRoutineTitle').value = '';
    modalAddRoutine.showModal();
  });

  document.getElementById('btnSaveNewRoutine').addEventListener('click', () => {
    const title = document.getElementById('newRoutineTitle').value.trim();
    if (!title) return alert('Please enter a routine title');
    const bucket = document.getElementById('newRoutineBucket').value;

    state.routines.push({
      id: Date.now(),
      kidId: state.activeKidId,
      title,
      bucket,
      done: false
    });
    saveState();
    modalAddRoutine.close();
    renderRoutines();
    playChime(true);
  });

  // --- Chores & Stars Rendering ---
  function renderChores() {
    const kid = getActiveKid();
    elChoresTitle.textContent = `${kid.name}'s Chores & Star Rewards`;
    elStarSummaryText.innerHTML = `${kid.name} has earned <strong>${kid.points} Stars</strong>! Keep it up!`;

    const kidChores = state.chores.filter(c => c.kidId === kid.id);
    elChoreGrid.innerHTML = '';

    kidChores.forEach(chore => {
      const card = document.createElement('div');
      card.className = 'chore-card';

      card.innerHTML = `
        <div class="chore-card-top">
          <span class="chore-icon">${chore.icon || '🧹'}</span>
          <span class="chore-title">${chore.title}</span>
          <span class="chore-pts-badge">+${chore.points} pts</span>
        </div>
        <div class="chore-card-actions">
          <button class="btn-delete-item btn-del-chore" title="Delete chore">🗑️</button>
          <button class="btn-complete-chore"><span>⭐</span> Complete</button>
        </div>
      `;

      card.querySelector('.btn-del-chore').addEventListener('click', (e) => {
        e.stopPropagation();
        if (confirm(`Delete chore "${chore.title}"?`)) {
          state.chores = state.chores.filter(c => c.id !== chore.id);
          saveState();
          renderChores();
        }
      });

      card.querySelector('.btn-complete-chore').addEventListener('click', (e) => {
        const btn = e.currentTarget;
        kid.points += chore.points;
        saveState();
        playChime(true);

        btn.classList.add('animate-pop');
        setTimeout(() => btn.classList.remove('animate-pop'), 400);

        renderKidSelector();
        elStarSummaryText.innerHTML = `${kid.name} has earned <strong>${kid.points} Stars</strong>! Keep it up!`;
      });

      elChoreGrid.appendChild(card);
    });
  }

  // Add Chore Modal
  document.getElementById('btnAddChore').addEventListener('click', () => {
    document.getElementById('newChoreTitle').value = '';
    modalAddChore.showModal();
  });

  document.getElementById('btnSaveNewChore').addEventListener('click', () => {
    const title = document.getElementById('newChoreTitle').value.trim();
    if (!title) return alert('Please enter a chore title');
    const category = document.getElementById('newChoreCategory').value;
    const points = parseInt(document.getElementById('newChorePoints').value, 10) || 10;

    const iconMap = {
      Bed: '🛏️',
      Toys: '🧸',
      Pet: '🐱',
      Dishes: '🍽️',
      Laundry: '🧺',
      Reading: '📚'
    };

    state.chores.push({
      id: Date.now(),
      kidId: state.activeKidId,
      title,
      category,
      icon: iconMap[category] || '⭐',
      points
    });

    saveState();
    modalAddChore.close();
    renderChores();
    playChime(true);
  });

  // --- Wall Calendar Rendering ---
  function renderCalendar() {
    const year = currentCalDate.getFullYear();
    const month = currentCalDate.getMonth();
    const monthNames = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];
    elCalMonthTitle.textContent = `${monthNames[month]} ${year}`;

    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const daysInPrevMonth = new Date(year, month, 0).getDate();

    elCalGrid.innerHTML = '';
    const today = new Date();

    // Previous month filler days
    for (let i = firstDay - 1; i >= 0; i--) {
      const cell = document.createElement('div');
      cell.className = 'cal-cell other-month';
      cell.innerHTML = `<span class="cal-date-num">${daysInPrevMonth - i}</span>`;
      elCalGrid.appendChild(cell);
    }

    // Current month days
    for (let day = 1; day <= daysInMonth; day++) {
      const cell = document.createElement('div');
      const thisDate = new Date(year, month, day);
      const isToday = thisDate.toDateString() === today.toDateString();
      const isSelected = thisDate.toDateString() === selectedCalDay.toDateString();

      cell.className = `cal-cell ${isToday ? 'today' : ''} ${isSelected ? 'selected' : ''}`;

      const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
      const dayEvents = state.events.filter(e => e.date === dateStr);

      let dotsHtml = '';
      dayEvents.forEach(e => {
        dotsHtml += `<span class="event-dot ${e.category.toLowerCase()}" title="${e.title}"></span>`;
      });

      cell.innerHTML = `
        <div class="cal-cell-header">
          <span class="cal-date-num">${day}</span>
          ${isToday ? '<span class="cal-today-badge">TODAY</span>' : ''}
        </div>
        <div class="cal-events-dots">${dotsHtml}</div>
      `;

      cell.addEventListener('click', () => {
        selectedCalDay = thisDate;
        renderCalendar();
        renderAgenda();
      });

      elCalGrid.appendChild(cell);
    }

    // Trailing blank cells to complete the 7-column grid
    const totalCells = firstDay + daysInMonth;
    const remaining = 7 - (totalCells % 7);
    if (remaining < 7) {
      for (let i = 1; i <= remaining; i++) {
        const cell = document.createElement('div');
        cell.className = 'cal-cell other-month';
        cell.innerHTML = `<span class="cal-date-num">${i}</span>`;
        elCalGrid.appendChild(cell);
      }
    }

    renderAgenda();
  }

  function renderAgenda() {
    const year = selectedCalDay.getFullYear();
    const month = selectedCalDay.getMonth() + 1;
    const day = selectedCalDay.getDate();
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

    const formattedDate = selectedCalDay.toLocaleDateString([], {
      weekday: 'long',
      month: 'short',
      day: 'numeric'
    });
    elAgendaTitle.textContent = `Schedule for ${formattedDate}`;

    const events = state.events.filter(e => e.date === dateStr);
    elAgendaEvents.innerHTML = '';

    if (events.length === 0) {
      elAgendaEvents.innerHTML = `<span style="font-size: 13px; color: var(--text-dim);">No appointments or activities scheduled for this day.</span>`;
      return;
    }

    events.forEach(event => {
      const kid = state.kids.find(k => k.id === event.kidId);
      const kidName = kid ? kid.name : 'All Family';

      const row = document.createElement('div');
      row.className = 'agenda-event-row';
      row.innerHTML = `
        <div>
          <strong style="font-size: 14px;">${event.title}</strong>
          <div style="font-size: 12px; color: var(--text-muted); margin-top: 2px;">
            ⏰ ${event.time} • 👤 ${kidName} • <span class="event-dot ${event.category.toLowerCase()}" style="display:inline-block; vertical-align:middle;"></span> ${event.category}
          </div>
        </div>
        <button class="btn-delete-item btn-del-event" title="Delete event">🗑️</button>
      `;

      row.querySelector('.btn-del-event').addEventListener('click', () => {
        if (confirm(`Delete event "${event.title}"?`)) {
          state.events = state.events.filter(e => e.id !== event.id);
          saveState();
          renderCalendar();
        }
      });

      elAgendaEvents.appendChild(row);
    });
  }

  elCalPrev.addEventListener('click', () => {
    currentCalDate.setMonth(currentCalDate.getMonth() - 1);
    renderCalendar();
  });

  elCalNext.addEventListener('click', () => {
    currentCalDate.setMonth(currentCalDate.getMonth() + 1);
    renderCalendar();
  });

  // Add Event Modal
  document.getElementById('btnAddEvent').addEventListener('click', () => {
    document.getElementById('newEventTitle').value = '';
    const selYear = selectedCalDay.getFullYear();
    const selMonth = String(selectedCalDay.getMonth() + 1).padStart(2, '0');
    const selDay = String(selectedCalDay.getDate()).padStart(2, '0');
    document.getElementById('newEventDate').value = `${selYear}-${selMonth}-${selDay}`;

    const kidSelect = document.getElementById('newEventKid');
    kidSelect.innerHTML = `<option value="0">All Family</option>`;
    state.kids.forEach(k => {
      kidSelect.innerHTML += `<option value="${k.id}" ${k.id === state.activeKidId ? 'selected' : ''}>${k.name}</option>`;
    });

    modalAddEvent.showModal();
  });

  document.getElementById('btnSaveNewEvent').addEventListener('click', () => {
    const title = document.getElementById('newEventTitle').value.trim();
    if (!title) return alert('Please enter an event title');
    const date = document.getElementById('newEventDate').value;
    const time = document.getElementById('newEventTime').value || '12:00';
    const kidId = parseInt(document.getElementById('newEventKid').value, 10);
    const category = document.getElementById('newEventCategory').value;

    state.events.push({
      id: Date.now(),
      title,
      date,
      time,
      kidId,
      category
    });

    saveState();
    modalAddEvent.close();
    renderCalendar();
    playChime(true);
  });

  // --- Family Frame Gallery ---
  function renderGallery() {
    if (state.photos.length === 0) return;
    const currentPhoto = state.photos[activeGalleryIndex % state.photos.length];

    elGalleryMainImg.src = currentPhoto.url;
    elGalleryCaption.textContent = currentPhoto.caption;
    elGalleryDate.textContent = currentPhoto.date;

    // Ambient frame in right pane
    elAmbientPhotoImg.src = currentPhoto.url;
    elAmbientCaption.textContent = currentPhoto.caption;
    elAmbientSub.textContent = currentPhoto.date;

    elGalleryThumbs.innerHTML = '';
    state.photos.forEach((photo, idx) => {
      const thumb = document.createElement('div');
      thumb.className = `gallery-thumb ${idx === activeGalleryIndex ? 'active' : ''}`;
      thumb.innerHTML = `<img src="${photo.url}" alt="${photo.caption}">`;
      thumb.addEventListener('click', () => {
        activeGalleryIndex = idx;
        renderGallery();
      });
      elGalleryThumbs.appendChild(thumb);
    });
  }

  elBtnToggleSlideshow.addEventListener('click', () => {
    if (slideshowInterval) {
      clearInterval(slideshowInterval);
      slideshowInterval = null;
      elBtnToggleSlideshow.textContent = '▶ Start Slideshow';
    } else {
      slideshowInterval = setInterval(() => {
        activeGalleryIndex = (activeGalleryIndex + 1) % state.photos.length;
        renderGallery();
      }, 5000);
      elBtnToggleSlideshow.textContent = '⏸ Pause Slideshow';
    }
  });

  // Add Photo Modal
  document.getElementById('btnAddPhotoFull').addEventListener('click', () => modalAddPhoto.showModal());
  document.getElementById('btnAddPhotoAmbient').addEventListener('click', () => modalAddPhoto.showModal());

  let selectedPresetPhotoUrl = null;
  const samplePresets = {
    nature: 'https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=1200&q=80',
    beach: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1200&q=80',
    birthday: 'https://images.unsplash.com/photo-1464349095431-e9a21285b5f3?auto=format&fit=crop&w=1200&q=80'
  };

  document.querySelectorAll('.sample-photo-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const key = btn.getAttribute('data-preset');
      selectedPresetPhotoUrl = samplePresets[key];
      document.getElementById('newPhotoCaption').value = btn.textContent;
    });
  });

  document.getElementById('btnSaveNewPhoto').addEventListener('click', () => {
    const caption = document.getElementById('newPhotoCaption').value.trim() || 'Family Memory';
    const fileInput = document.getElementById('newPhotoFileInput');

    if (fileInput.files && fileInput.files[0]) {
      const reader = new FileReader();
      reader.onload = (e) => {
        state.photos.push({
          id: Date.now(),
          url: e.target.result,
          caption,
          date: '2026'
        });
        saveState();
        modalAddPhoto.close();
        renderGallery();
        playChime(true);
      };
      reader.readAsDataURL(fileInput.files[0]);
    } else if (selectedPresetPhotoUrl) {
      state.photos.push({
        id: Date.now(),
        url: selectedPresetPhotoUrl,
        caption,
        date: '2026'
      });
      selectedPresetPhotoUrl = null;
      saveState();
      modalAddPhoto.close();
      renderGallery();
      playChime(true);
    } else {
      alert('Please upload an image file or pick a preset');
    }
  });

  // --- Right Pane Mode Switch ---
  elModeAi.addEventListener('click', () => {
    elModeAi.classList.add('active');
    elModePhoto.classList.remove('active');
    elViewAi.classList.add('active');
    elViewPhoto.classList.remove('active');
  });

  elModePhoto.addEventListener('click', () => {
    elModePhoto.classList.add('active');
    elModeAi.classList.remove('active');
    elViewPhoto.classList.add('active');
    elViewAi.classList.remove('active');
  });

  // --- AI Brain Query & Memory Retrieval Engine ---
  function queryAiBrain(queryText) {
    if (!queryText.trim()) return;

    // Append user bubble
    const userBubble = document.createElement('div');
    userBubble.className = 'chat-bubble user';
    userBubble.textContent = queryText;
    elChatMessages.appendChild(userBubble);
    elChatMessages.scrollTop = elChatMessages.scrollHeight;

    const kid = getActiveKid();
    const qLower = queryText.toLowerCase();

    // Match against notes and routines
    let matchedNotes = state.notes.filter(note => {
      const nLower = note.toLowerCase();
      return qLower.split(' ').some(word => word.length > 3 && nLower.includes(word));
    });

    let responseText = '';
    if (qLower.includes('bedtime') || qLower.includes('sleep') || qLower.includes('night')) {
      responseText = `🌙 Bedtime protocol for ${kid.name}: Dim lights 30 mins prior, use weighted blanket, and run through the visual first-then card.`;
    } else if (qLower.includes('sensory') || qLower.includes('overload') || qLower.includes('trigger')) {
      responseText = `🧩 Sensory accommodations for ${kid.name}: Check socks for inside-out seams, offer noise-canceling headphones, and provide deep pressure hugs when overwhelmed.`;
    } else if (qLower.includes('reward') || qLower.includes('star') || qLower.includes('point')) {
      responseText = `⭐ Star Reward Rule: ${kid.name} currently has ${kid.points} points. Every completed chore earns 10-15 stars toward chosen weekend privileges.`;
    } else if (qLower.includes('food') || qLower.includes('eat') || qLower.includes('meal') || qLower.includes('breakfast')) {
      responseText = `🥣 Meal notes for ${kid.name}: Warm oatmeal with maple drizzle is preferred. Keep textures separated.`;
    } else if (matchedNotes.length > 0) {
      responseText = `📋 Grounded Memory from Family Notes:\n"${matchedNotes[0]}"`;
    } else {
      responseText = `🤖 Routines Brain: Logged knowledge for ${kid.name}. You can record a new voice note using the 🎙️ button to train the household memory engine!`;
    }

    setTimeout(() => {
      const aiBubble = document.createElement('div');
      aiBubble.className = 'chat-bubble ai';
      aiBubble.innerHTML = `<strong>Routines Brain:</strong> ${responseText.replace(/\n/g, '<br>')}`;
      elChatMessages.appendChild(aiBubble);
      elChatMessages.scrollTop = elChatMessages.scrollHeight;
      playChime(true);
    }, 300);
  }

  elBtnAiSend.addEventListener('click', () => {
    const text = elAiInput.value.trim();
    if (text) {
      queryAiBrain(text);
      elAiInput.value = '';
    }
  });

  elAiInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      const text = elAiInput.value.trim();
      if (text) {
        queryAiBrain(text);
        elAiInput.value = '';
      }
    }
  });

  document.querySelectorAll('.quick-chips .chip').forEach(chip => {
    chip.addEventListener('click', () => {
      const query = chip.getAttribute('data-query');
      queryAiBrain(query);
    });
  });

  // Voice Note Modal
  elBtnVoiceLog.addEventListener('click', () => {
    document.getElementById('voiceNoteText').value = '';
    modalVoiceNote.showModal();
  });

  document.getElementById('btnSaveVoiceNote').addEventListener('click', () => {
    const note = document.getElementById('voiceNoteText').value.trim();
    if (!note) return alert('Please enter or dictate a note');
    state.notes.push(note);
    saveState();
    modalVoiceNote.close();

    const bubble = document.createElement('div');
    bubble.className = 'chat-bubble ai';
    bubble.innerHTML = `<strong>🎙️ Voice Log Saved to Memory:</strong> "${note}"`;
    elChatMessages.appendChild(bubble);
    elChatMessages.scrollTop = elChatMessages.scrollHeight;
    playChime(true);
  });

  // Close modals when clicking close buttons
  document.querySelectorAll('[data-close]').forEach(btn => {
    btn.addEventListener('click', () => {
      const targetId = btn.getAttribute('data-close');
      const dialog = document.getElementById(targetId);
      if (dialog) dialog.close();
    });
  });

  // --- Initial Render ---
  function renderAll() {
    renderKidSelector();
    renderRoutines();
    renderChores();
    renderCalendar();
    renderGallery();
  }

  applyTheme(state.theme || 'dark');
  renderAll();

})();
