## Summary

<!-- What changed and why. Link the issue: Fixes #123. -->

## How it was tested

<!-- Paste the commands and their result. -->

- [ ] `./build.sh test`
- [ ] If the change needs new shim members or a call site, the FerroLoom pull request is linked:
      <!-- FerroLoom PR URL -->

## Checklist

- [ ] Every commit is signed off (`git commit -s`, DCO).
- [ ] Adapted Fabric API code keeps its Apache-2.0 headers; no Mojang code or assets.
- [ ] The module keeps upstream semantics (when the event fires, what it passes, what a listener
      may do), not only the signatures.
- [ ] `README.md`, `docs/compatibility.md` and `CHANGELOG.md` updated when coverage or behavior
      changed.
- [ ] Comments and documentation are in English.