#![doc = include_str!("../README.md")]

use std::f32::consts::PI;
use std::time::{Duration, Instant};

use macroquad::prelude::*;
use macroquad::rand::RandomRange;

const GRID_SIZE: f32 = 16.0;
const STAR_COLOR: Color = color_u8!(255, 254, 242, 255);
const NUMBER_OF_STARS: usize = 125;
const TOTAL_FIREWORKS: usize = 25;

fn hsba_to_rgba(h: f32, s: f32, b: f32, a: f32) -> Color {
    if s == 0.0 {
        Color::new(b, b, b, a)
    } else {
        let h = (h % 1.0) * 6.0;
        let f = h.fract();
        let p = b * (1.0 - s);
        let q = b * (1.0 - s * f);
        let t = b * (1.0 - (s * (1.0 - f)));
        match h as u32 {
            0 => Color::new(b, t, p, a),
            1 => Color::new(q, b, p, a),
            2 => Color::new(p, b, t, a),
            3 => Color::new(p, q, b, a),
            4 => Color::new(t, p, b, a),
            5 => Color::new(b, p, q, a),
            _ => unreachable!(),
        }
    }
}

struct Starfield {
    stars: [Vec2; NUMBER_OF_STARS],
}

impl Starfield {
    pub fn new() -> Self {
        let mut stars = [Vec2::default(); NUMBER_OF_STARS];
        for star in &mut stars {
            star.x = f32::gen_range(0.0, 1.0);
            star.y = f32::gen_range(0.0, 1.0);
        }
        Self { stars }
    }

    pub fn paint(&self, screen_width: f32, screen_height: f32, offset: Vec2) {
        let offset = offset * 0.2 * GRID_SIZE;
        for star in &self.stars {
            draw_circle(
                star.x * screen_width + offset.x,
                star.y * screen_height + offset.y,
                2.0,
                STAR_COLOR,
            );
        }
    }
}

struct Firework {
    pos: Vec2,
    color: Color,
    size: u8,
    counter: u8,
    start: Instant,
    step: Duration,
}

impl Firework {
    pub fn new(now: Instant) -> Self {
        Self {
            pos: vec2(
                // the original algorithm used was biased, so we replicate that here
                rand::gen_range(-0.5, 1.5),
                rand::gen_range(-0.5, 1.5),
            ),
            color: hsba_to_rgba(
                rand::gen_range(0.0, 1.0),
                rand::gen_range(0.6, 1.0),
                rand::gen_range(0.8, 1.0),
                rand::gen_range(0.65, 0.9),
            ),
            size: rand::gen_range(6, 16),
            counter: 1,
            start: now,
            step: Duration::from_millis(rand::gen_range(15, 51)),
        }
    }

    pub fn paint(&self, screen_width: f32, screen_height: f32, offset: Vec2) {
        let offset = offset * GRID_SIZE + Vec2::splat(self.size as f32 - 8.0) / 32.0;
        for c in self.counter.saturating_sub(2)..=self.counter {
            for d in (0..360).step_by(36) {
                draw_circle(
                    offset.x + self.pos.x * screen_width + (d as f32 * PI / 180.0).sin() * c as f32 * self.size as f32,
                    offset.y + self.pos.y * screen_height + (d as f32 * PI / 180.0).cos() * c as f32 * self.size as f32,
                    6.0,
                    self.color,
                );
            }
        }
    }

    pub fn step(&mut self, now: Instant) -> bool {
        if now - self.start > self.step {
            self.counter += 1;
            if self.counter > 10 {
                return false;
            }
            self.start = now;
        }
        true
    }
}

fn window_conf() -> Conf {
    Conf {
        window_title: "Fireworks - Happy 4th of July!".into(),
        fullscreen: true,
        ..Conf::default()
    }
}

#[macroquad::main(window_conf())]
async fn main() {
    // let font = load_ttf_font_from_bytes(include_bytes!("FredokaOne-Regular.ttf")).unwrap();

    let mut mouse_offset = Vec2::default();
    let mut mouse_accel = Vec2::default();
    let starfield = Starfield::new();
    let mut now = Instant::now();
    let mut fireworks: [Firework; TOTAL_FIREWORKS] = core::array::from_fn(|_| Firework::new(now));

    set_cursor_grab(true);
    show_mouse(false);

    loop {
        if is_key_down(KeyCode::Escape) {
            return;
        }

        let screen_width = screen_width();
        let screen_height = screen_height();
        let mouse_delta = mouse_delta_position();
        if (mouse_offset.x + mouse_delta.x).abs() < screen_width / GRID_SIZE * 0.5
            || mouse_delta.x.signum() == mouse_offset.x.signum()
        {
            mouse_accel.x -= mouse_delta.x;
        }
        if (mouse_offset.y + mouse_delta.y).abs() < screen_height / GRID_SIZE * 0.5
            || mouse_delta.y.signum() == mouse_offset.y.signum()
        {
            mouse_accel.y -= mouse_delta.y;
        }
        mouse_offset += mouse_accel;
        mouse_accel *= 0.9;

        clear_background(BLACK);
        starfield.paint(screen_width, screen_height, mouse_offset);
        now = Instant::now();
        for firework in &mut fireworks {
            firework.paint(screen_width, screen_height, mouse_offset);
            if !firework.step(now) {
                *firework = Firework::new(now);
            }
        }
        next_frame().await
    }
}
