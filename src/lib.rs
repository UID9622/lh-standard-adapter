//! lh-standard-adapter — LongHun Standard Adapter
//! DNA: #LongHun⚡️BingWu·GuiWei·JiaZi·ZiShi·䷾JiJi-ADAPTER-v1.0.0

pub mod dna;
pub mod audit;
pub mod adapter;

pub use adapter::{LongHunAdapter, wrap};
pub use dna::{DNAGenerator, generate_dna};
pub use audit::{AuditWrapper, audit_wrap};
